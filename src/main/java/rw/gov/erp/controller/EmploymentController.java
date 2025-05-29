package rw.gov.erp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rw.gov.erp.dto.employment.CreateEmploymentRequest;
import rw.gov.erp.dto.employment.EmploymentResponse;
import rw.gov.erp.service.EmploymentService;

import java.util.List;

@RestController
@RequestMapping("/api/employments")
@RequiredArgsConstructor
@Tag(name = "Employment", description = "Employment management APIs")
public class EmploymentController {

    private final EmploymentService employmentService;

    @Operation(summary = "Create employment", description = "Creates a new employment record for an employee")
    @PostMapping
    public ResponseEntity<EmploymentResponse> createEmployment(@Valid @RequestBody CreateEmploymentRequest request) {
        return ResponseEntity.ok(employmentService.createEmployment(request));
    }

    @Operation(summary = "Get employments by employee", description = "Retrieves all employment records for a specific employee")
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<EmploymentResponse>> getEmploymentsByEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(employmentService.getEmploymentsByEmployeeId(employeeId));
    }

    @Operation(summary = "Get active employments", description = "Retrieves all active employment records")
    @GetMapping("/active")
    public ResponseEntity<List<EmploymentResponse>> getActiveEmployments() {
        return ResponseEntity.ok(employmentService.getActiveEmployments());
    }

    @Operation(summary = "Deactivate employment", description = "Deactivates an employment record")
    @PostMapping("/{employmentId}/deactivate")
    public ResponseEntity<Void> deactivateEmployment(@PathVariable Long employmentId) {
        employmentService.deactivateEmployment(employmentId);
        return ResponseEntity.ok().build();
    }
}
