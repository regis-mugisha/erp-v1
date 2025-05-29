package rw.gov.erp.dto.deduction;

import lombok.Data;
import rw.gov.erp.model.Deduction;

import java.math.BigDecimal;

@Data
public class DeductionResponse {
    private Long id;
    private String deductionName;
    private BigDecimal percentage;

    public static DeductionResponse fromEntity(Deduction deduction) {
        DeductionResponse response = new DeductionResponse();
        response.setId(deduction.getId());
        response.setDeductionName(deduction.getDeductionName());
        response.setPercentage(deduction.getPercentage());
        return response;
    }
}
