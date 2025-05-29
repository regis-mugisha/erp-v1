package rw.gov.erp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rw.gov.erp.dto.employment.EmploymentRequest;
import rw.gov.erp.dto.employment.EmploymentResponse;
import rw.gov.erp.service.EmploymentService;

import java.util.List;

@RestController
@RequestMapping("/api/employments")
@RequiredArgsConstructor
@Tag(name = "Employment Management", description = "APIs for managing employee employment details")
@SecurityRequirement(name = "Bearer Authentication")
public class EmploymentController {

    private final EmploymentService employmentService;

    @Operation(summary = "Create new employment", description = "Creates a new employment record for an employee")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<EmploymentResponse> createEmployment(@Valid @RequestBody EmploymentRequest request) {
        return ResponseEntity.ok(employmentService.createEmployment(request));
    }

    @Operation(summary = "Get employee employments", description = "Retrieves all employment records for a specific employee")
    @GetMapping("/employee/{employeeCode}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or @securityService.isCurrentUser(#employeeCode)")
    public ResponseEntity<List<EmploymentResponse>> getEmploymentsByEmployeeCode(@PathVariable String employeeCode) {
        return ResponseEntity.ok(employmentService.getEmploymentsByEmployeeCode(employeeCode));
    }

    @Operation(summary = "Get active employment", description = "Retrieves the active employment record for a specific employee")
    @GetMapping("/employee/{employeeCode}/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or @securityService.isCurrentUser(#employeeCode)")
    public ResponseEntity<EmploymentResponse> getActiveEmployment(@PathVariable String employeeCode) {
        return ResponseEntity.ok(employmentService.getActiveEmployment(employeeCode));
    }

    @Operation(summary = "Update employment", description = "Updates an existing employment record")
    @PutMapping("/{code}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<EmploymentResponse> updateEmployment(
            @PathVariable String code,
            @Valid @RequestBody EmploymentRequest request) {
        return ResponseEntity.ok(employmentService.updateEmployment(code, request));
    }

    @Operation(summary = "Deactivate employment", description = "Deactivates an employment record")
    @DeleteMapping("/{code}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivateEmployment(@PathVariable String code) {
        employmentService.deactivateEmployment(code);
        return ResponseEntity.noContent().build();
    }
}
