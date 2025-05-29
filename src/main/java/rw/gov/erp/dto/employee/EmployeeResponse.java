package rw.gov.erp.dto.employee;

import lombok.Data;
import rw.gov.erp.model.Employee;

import java.time.LocalDate;
import java.util.List;

@Data
public class EmployeeResponse {
    private String code;
    private String firstName;
    private String lastName;
    private String email;
    private List<String> roles;
    private String mobile;
    private LocalDate dateOfBirth;
    private Employee.EmployeeStatus status;

    public static EmployeeResponse fromEntity(Employee employee) {
        EmployeeResponse response = new EmployeeResponse();
        response.setCode(employee.getCode());
        response.setFirstName(employee.getFirstName());
        response.setLastName(employee.getLastName());
        response.setEmail(employee.getEmail());
        response.setRoles(employee.getRoles());
        response.setMobile(employee.getMobile());
        response.setDateOfBirth(employee.getDateOfBirth());
        response.setStatus(employee.getStatus());
        return response;
    }
}
