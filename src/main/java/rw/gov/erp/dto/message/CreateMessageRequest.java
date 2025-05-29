package rw.gov.erp.dto.message;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateMessageRequest {
    @NotNull(message = "Payslip ID is required")
    private Long payslipId;

    @NotNull(message = "Message content is required")
    private String message;
} 