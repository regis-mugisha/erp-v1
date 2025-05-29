package rw.gov.erp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.gov.erp.dto.employment.EmploymentRequest;
import rw.gov.erp.dto.employment.EmploymentResponse;
import rw.gov.erp.exception.NotFoundException;
import rw.gov.erp.model.Employee;
import rw.gov.erp.model.Employment;
import rw.gov.erp.repository.EmployeeRepository;
import rw.gov.erp.repository.EmploymentRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmploymentService {

    private final EmploymentRepository employmentRepository;
    private final EmployeeRepository employeeRepository;

    @Transactional
    public EmploymentResponse createEmployment(EmploymentRequest request) {
        Employee employee = employeeRepository.findById(request.getEmployeeCode())
                .orElseThrow(() -> {
                    log.warn("Employee not found for employment: {}", request.getEmployeeCode());
                    return new NotFoundException("Employee not found");
                });

        // Deactivate any existing active employment
        employmentRepository.findByEmployeeCodeAndStatus(request.getEmployeeCode(), Employment.EmploymentStatus.ACTIVE)
                .ifPresent(existingEmployment -> {
                    existingEmployment.setStatus(Employment.EmploymentStatus.INACTIVE);
                    employmentRepository.save(existingEmployment);
                    log.info("Deactivated previous active employment for employee: {}", request.getEmployeeCode());
                });

        Employment employment = new Employment();
        employment.setEmployee(employee);
        employment.setDepartment(request.getDepartment());
        employment.setPosition(request.getPosition());
        employment.setBaseSalary(request.getBaseSalary());
        employment.setJoiningDate(request.getJoiningDate());

        Employment saved = employmentRepository.save(employment);
        log.info("Created new employment for employee: {}", request.getEmployeeCode());
        return EmploymentResponse.fromEntity(saved);
    }

    public List<EmploymentResponse> getEmploymentsByEmployeeCode(String employeeCode) {
        return employmentRepository.findByEmployeeCode(employeeCode).stream()
                .map(EmploymentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public EmploymentResponse getActiveEmployment(String employeeCode) {
        return employmentRepository.findByEmployeeCodeAndStatus(employeeCode, Employment.EmploymentStatus.ACTIVE)
                .map(EmploymentResponse::fromEntity)
                .orElseThrow(() -> {
                    log.warn("No active employment found for employee: {}", employeeCode);
                    return new NotFoundException("No active employment found");
                });
    }

    @Transactional
    public EmploymentResponse updateEmployment(String code, EmploymentRequest request) {
        Employment employment = employmentRepository.findById(code)
                .orElseThrow(() -> {
                    log.warn("Employment not found for update: {}", code);
                    return new NotFoundException("Employment not found");
                });

        employment.setDepartment(request.getDepartment());
        employment.setPosition(request.getPosition());
        employment.setBaseSalary(request.getBaseSalary());

        Employment updated = employmentRepository.save(employment);
        log.info("Updated employment: {}", code);
        return EmploymentResponse.fromEntity(updated);
    }

    @Transactional
    public void deactivateEmployment(String code) {
        Employment employment = employmentRepository.findById(code)
                .orElseThrow(() -> {
                    log.warn("Employment not found for deactivation: {}", code);
                    return new NotFoundException("Employment not found");
                });

        employment.setStatus(Employment.EmploymentStatus.INACTIVE);
        employmentRepository.save(employment);
        log.info("Deactivated employment: {}", code);
    }
}
