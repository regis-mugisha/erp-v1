package rw.gov.erp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.gov.erp.dto.deduction.DeductionRequest;
import rw.gov.erp.dto.deduction.DeductionResponse;
import rw.gov.erp.exception.BadRequestException;
import rw.gov.erp.exception.NotFoundException;
import rw.gov.erp.model.Deduction;
import rw.gov.erp.repository.DeductionRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeductionService {

    private final DeductionRepository deductionRepository;

    @Transactional
    public DeductionResponse createDeduction(DeductionRequest request) {
        if (deductionRepository.existsByDeductionName(request.getDeductionName())) {
            log.warn("Attempt to create deduction with existing name: {}", request.getDeductionName());
            throw new BadRequestException("Deduction name already exists");
        }

        Deduction deduction = new Deduction();
        deduction.setDeductionName(request.getDeductionName());
        deduction.setPercentage(request.getPercentage());

        Deduction saved = deductionRepository.save(deduction);
        log.info("Created new deduction: {}", saved.getDeductionName());
        return DeductionResponse.fromEntity(saved);
    }

    public List<DeductionResponse> getAllDeductions() {
        return deductionRepository.findAll().stream()
                .map(DeductionResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public DeductionResponse getDeductionByCode(String code) {
        return deductionRepository.findById(code)
                .map(DeductionResponse::fromEntity)
                .orElseThrow(() -> {
                    log.warn("Deduction not found: {}", code);
                    return new NotFoundException("Deduction not found");
                });
    }

    @Transactional
    public DeductionResponse updateDeduction(String code, DeductionRequest request) {
        Deduction deduction = deductionRepository.findById(code)
                .orElseThrow(() -> {
                    log.warn("Deduction not found for update: {}", code);
                    return new NotFoundException("Deduction not found");
                });

        if (!deduction.getDeductionName().equals(request.getDeductionName()) &&
                deductionRepository.existsByDeductionName(request.getDeductionName())) {
            log.warn("Attempt to update deduction to existing name: {}", request.getDeductionName());
            throw new BadRequestException("Deduction name already exists");
        }

        deduction.setDeductionName(request.getDeductionName());
        deduction.setPercentage(request.getPercentage());

        Deduction updated = deductionRepository.save(deduction);
        log.info("Updated deduction: {}", code);
        return DeductionResponse.fromEntity(updated);
    }

    @Transactional
    public void deleteDeduction(String code) {
        if (!deductionRepository.existsById(code)) {
            log.warn("Attempt to delete non-existent deduction: {}", code);
            throw new NotFoundException("Deduction not found");
        }
        deductionRepository.deleteById(code);
        log.info("Deleted deduction: {}", code);
    }
}
