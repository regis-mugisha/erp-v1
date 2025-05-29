package rw.gov.erp.dto.employment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateEmploymentRequest {
    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    @NotNull(message = "Department is required")
    private String department;

    @NotNull(message = "Position is required")
    private String position;

    @NotNull(message = "Base salary is required")
    @Positive(message = "Base salary must be positive")
    private BigDecimal baseSalary;

    @NotNull(message = "Joining date is required")
    private LocalDate joiningDate;
} 