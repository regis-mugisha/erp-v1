package rw.gov.erp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rw.gov.erp.dto.deduction.DeductionRequest;
import rw.gov.erp.dto.deduction.DeductionResponse;
import rw.gov.erp.service.DeductionService;

import java.util.List;

@RestController
@RequestMapping("/api/deductions")
@RequiredArgsConstructor
@Tag(name = "Deduction Management", description = "APIs for managing salary deductions")
@SecurityRequirement(name = "Bearer Authentication")
public class DeductionController {

    private final DeductionService deductionService;

    @Operation(summary = "Create a new deduction", description = "Creates a new deduction type with specified percentage")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DeductionResponse> createDeduction(@Valid @RequestBody DeductionRequest request) {
        return ResponseEntity.ok(deductionService.createDeduction(request));
    }

    @Operation(summary = "Get all deductions", description = "Retrieves a list of all deduction types")
    @GetMapping
    public ResponseEntity<List<DeductionResponse>> getAllDeductions() {
        return ResponseEntity.ok(deductionService.getAllDeductions());
    }

    @Operation(summary = "Get deduction by code", description = "Retrieves a specific deduction type by its code")
    @GetMapping("/{code}")
    public ResponseEntity<DeductionResponse> getDeductionByCode(@PathVariable String code) {
        return ResponseEntity.ok(deductionService.getDeductionByCode(code));
    }

    @Operation(summary = "Update deduction", description = "Updates an existing deduction type")
    @PutMapping("/{code}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DeductionResponse> updateDeduction(
            @PathVariable String code,
            @Valid @RequestBody DeductionRequest request) {
        return ResponseEntity.ok(deductionService.updateDeduction(code, request));
    }

    @Operation(summary = "Delete deduction", description = "Deletes a deduction type by its code")
    @DeleteMapping("/{code}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDeduction(@PathVariable String code) {
        deductionService.deleteDeduction(code);
        return ResponseEntity.noContent().build();
    }
}
