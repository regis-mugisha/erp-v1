package rw.gov.erp.dto.employment;

import lombok.Data;
import rw.gov.erp.model.Employment;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class EmploymentResponse {
    private String code;
    private String employeeCode;
    private String department;
    private String position;
    private BigDecimal baseSalary;
    private Employment.EmploymentStatus status;
    private LocalDate joiningDate;

    public static EmploymentResponse fromEntity(Employment employment) {
        EmploymentResponse response = new EmploymentResponse();
        response.setCode(employment.getCode());
        response.setEmployeeCode(employment.getEmployee().getCode());
        response.setDepartment(employment.getDepartment());
        response.setPosition(employment.getPosition());
        response.setBaseSalary(employment.getBaseSalary());
        response.setStatus(employment.getStatus());
        response.setJoiningDate(employment.getJoiningDate());
        return response;
    }
}