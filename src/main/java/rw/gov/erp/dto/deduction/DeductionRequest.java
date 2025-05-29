package rw.gov.erp.dto.deduction;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DeductionRequest {

    @NotBlank(message = "Deduction name is required")
    private String deductionName;

    @NotNull(message = "Percentage is required")
    @Positive(message = "Percentage must be positive")
    private BigDecimal percentage;
}