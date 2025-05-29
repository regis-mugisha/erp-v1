package rw.gov.erp.dto.payslip;

import lombok.Data;
import rw.gov.erp.model.Payslip;

import java.math.BigDecimal;

@Data
public class PayslipResponse {
    private String id;
    private String employeeCode;
    private String employeeName;
    private BigDecimal baseSalary;
    private BigDecimal houseAmount;
    private BigDecimal transportAmount;
    private BigDecimal employeeTaxedAmount;
    private BigDecimal pensionAmount;
    private BigDecimal medicalInsuranceAmount;
    private BigDecimal otherTaxedAmount;
    private BigDecimal grossSalary;
    private BigDecimal netSalary;
    private Integer month;
    private Integer year;
    private Payslip.PayslipStatus status;

    public static PayslipResponse fromEntity(Payslip payslip) {
        PayslipResponse response = new PayslipResponse();
        response.setId(payslip.getId());
        response.setEmployeeCode(payslip.getEmployee().getCode());
        response.setEmployeeName(payslip.getEmployee().getFirstName() + " " + payslip.getEmployee().getLastName());
        response.setHouseAmount(payslip.getHouseAmount());
        response.setTransportAmount(payslip.getTransportAmount());
        response.setEmployeeTaxedAmount(payslip.getEmployeeTaxedAmount());
        response.setPensionAmount(payslip.getPensionAmount());
        response.setMedicalInsuranceAmount(payslip.getMedicalInsuranceAmount());
        response.setOtherTaxedAmount(payslip.getOtherTaxedAmount());
        response.setGrossSalary(payslip.getGrossSalary());
        response.setNetSalary(payslip.getNetSalary());
        response.setMonth(payslip.getMonth());
        response.setYear(payslip.getYear());
        response.setStatus(payslip.getStatus());
        return response;
    }
}
