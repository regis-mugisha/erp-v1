package rw.gov.erp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.gov.erp.dto.payslip.PayslipResponse;
import rw.gov.erp.exception.BadRequestException;
import rw.gov.erp.exception.NotFoundException;
import rw.gov.erp.model.Deduction;
import rw.gov.erp.model.Employment;
import rw.gov.erp.model.Payslip;
import rw.gov.erp.repository.DeductionRepository;
import rw.gov.erp.repository.EmploymentRepository;
import rw.gov.erp.repository.PayslipRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service class for managing payslip-related operations.
 * Handles the generation, calculation, and management of employee payslips.
 *
 * Key Responsibilities:
 * - Process monthly payslips for all active employees
 * - Calculate various salary components (base salary, allowances, deductions)
 * - Handle payslip approval and status updates
 * - Generate payslip reports
 *
 * Salary Components:
 * - Base Salary: Employee's basic salary
 * - Housing Allowance: Calculated as percentage of base salary
 * - Transport Allowance: Calculated as percentage of base salary
 * - Deductions:
 *   - Employee Tax
 *   - Pension
 *   - Medical Insurance
 *   - Other deductions
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PayslipService {

    private final PayslipRepository payslipRepository;
    private final EmploymentRepository employmentRepository;
    private final DeductionRepository deductionRepository;

    /**
     * Processes payslips for all active employees for a given month and year.
     * Calculates all salary components and deductions.
     *
     * @param month Month for payslip generation
     * @param year Year for payslip generation
     * @return List of generated PayslipResponse objects
     */
    @Transactional
    public List<PayslipResponse> processPayslips(Integer month, Integer year) {
        // Get all active employments
        List<Employment> activeEmployments = employmentRepository.findAll().stream()
                .filter(employment -> employment.getStatus() == Employment.EmploymentStatus.ACTIVE)
                .toList();

        // Get all deductions
        Map<String, BigDecimal> deductions = deductionRepository.findAll().stream()
                .collect(Collectors.toMap(
                        Deduction::getDeductionName,
                        Deduction::getPercentage
                ));

        log.info("Processing payslips for month: {}, year: {}", month, year);
        return activeEmployments.stream()
                .map(employment -> createPayslip(employment, month, year, deductions))
                .map(PayslipResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Creates a payslip for a specific employee.
     * Calculates all salary components and deductions.
     *
     * @param employment Employee's employment record
     * @param month Month for payslip
     * @param year Year for payslip
     * @param deductions Map of deduction types and their percentages
     * @return Created Payslip entity
     * @throws BadRequestException if payslip already exists
     */
    private Payslip createPayslip(Employment employment, Integer month, Integer year, Map<String, BigDecimal> deductions) {
        // Check if payslip already exists
        if (payslipRepository.findByEmployeeIdAndMonthAndYear(employment.getEmployee().getId(), month, year).isPresent()) {
            log.warn("Payslip already exists for employee {} for {}/{}", employment.getEmployee().getId(), month, year);
            throw new BadRequestException("Payslip already exists for employee " + employment.getEmployee().getId() + " for " + month + "/" + year);
        }

        BigDecimal baseSalary = employment.getBaseSalary();
        if (baseSalary == null || baseSalary.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Invalid base salary for employee " + employment.getEmployee().getId());
        }

        BigDecimal houseAmount = calculateAmount(baseSalary, deductions.get("Housing"));
        BigDecimal transportAmount = calculateAmount(baseSalary, deductions.get("Transport"));
        BigDecimal grossSalary = baseSalary.add(houseAmount).add(transportAmount);

        BigDecimal employeeTaxedAmount = calculateAmount(baseSalary, deductions.get("Employee Tax"));
        BigDecimal pensionAmount = calculateAmount(baseSalary, deductions.get("Pension"));
        BigDecimal medicalInsuranceAmount = calculateAmount(baseSalary, deductions.get("Medical Insurance"));
        BigDecimal otherTaxedAmount = calculateAmount(baseSalary, deductions.get("Others"));

        BigDecimal netSalary = grossSalary
                .subtract(employeeTaxedAmount)
                .subtract(pensionAmount)
                .subtract(medicalInsuranceAmount)
                .subtract(otherTaxedAmount);

        Payslip payslip = new Payslip();
        payslip.setEmployee(employment.getEmployee());
        payslip.setBaseSalary(baseSalary);
        payslip.setHouseAmount(houseAmount);
        payslip.setTransportAmount(transportAmount);
        payslip.setEmployeeTaxedAmount(employeeTaxedAmount);
        payslip.setPensionAmount(pensionAmount);
        payslip.setMedicalInsuranceAmount(medicalInsuranceAmount);
        payslip.setOtherTaxedAmount(otherTaxedAmount);
        payslip.setGrossSalary(grossSalary);
        payslip.setNetSalary(netSalary);
        payslip.setMonth(month);
        payslip.setYear(year);
        payslip.setStatus(Payslip.PayslipStatus.PENDING);

        Payslip saved = payslipRepository.save(payslip);
        log.info("Created payslip for employee {} for {}/{}", employment.getEmployee().getId(), month, year);
        return saved;
    }

    /**
     * Calculates an amount based on a base amount and percentage.
     * Rounds to 2 decimal places using HALF_UP rounding mode.
     *
     * @param baseAmount Base amount for calculation
     * @param percentage Percentage to calculate
     * @return Calculated amount
     */
    private BigDecimal calculateAmount(BigDecimal baseAmount, BigDecimal percentage) {
        return baseAmount.multiply(percentage)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
    }

    /**
     * Retrieves all payslips for a specific employee.
     *
     * @param employeeId Employee's unique identifier
     * @return List of PayslipResponse objects
     */
    public List<PayslipResponse> getPayslipsByEmployeeId(Long employeeId) {
        return payslipRepository.findByEmployeeId(employeeId).stream()
                .map(PayslipResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all payslips for a specific month and year.
     *
     * @param month Month to filter by
     * @param year Year to filter by
     * @return List of PayslipResponse objects
     */
    public List<PayslipResponse> getPayslipsByMonthAndYear(Integer month, Integer year) {
        return payslipRepository.findByMonthAndYear(month, year).stream()
                .map(PayslipResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Approves all payslips for a specific month and year.
     * Marks them as PAID and triggers message generation.
     *
     * @param month Month of payslips to approve
     * @param year Year of payslips to approve
     */
    @Transactional
    public void approvePayslips(Integer month, Integer year) {
        List<Payslip> payslips = payslipRepository.findByMonthAndYear(month, year);
        payslips.forEach(payslip -> payslip.setStatus(Payslip.PayslipStatus.PAID));
        payslipRepository.saveAll(payslips);
        log.info("Approved payslips for month: {}, year: {}", month, year);
    }

    public byte[] generatePayslipPdf(Long payslipId) {
        Payslip payslip = payslipRepository.findById(payslipId)
                .orElseThrow(() -> new NotFoundException("Payslip not found with id: " + payslipId));
        // TODO: Implement PDF generation logic here
        return new byte[0]; // Placeholder return
    }
}