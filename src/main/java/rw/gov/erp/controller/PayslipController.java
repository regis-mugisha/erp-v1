package rw.gov.erp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rw.gov.erp.dto.payslip.PayslipResponse;
import rw.gov.erp.service.PayslipService;

import java.util.List;

@RestController
@RequestMapping("/api/payslips")
@RequiredArgsConstructor
@Tag(name = "Payslip Management", description = "APIs for managing employee payslips")
@SecurityRequirement(name = "Bearer Authentication")
public class PayslipController {

    private final PayslipService payslipService;

    @Operation(summary = "Process payslips", description = "Processes payslips for all active employees for a given month and year")
    @PostMapping("/process")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<PayslipResponse>> processPayslips(
            @RequestParam Integer month,
            @RequestParam Integer year) {
        return ResponseEntity.ok(payslipService.processPayslips(month, year));
    }

    @Operation(summary = "Get payslips by employee", description = "Retrieves all payslips for a specific employee")
    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or @securityService.isCurrentUser(#employeeId)")
    public ResponseEntity<List<PayslipResponse>> getPayslipsByEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(payslipService.getPayslipsByEmployeeId(employeeId));
    }

    @Operation(summary = "Get payslips by month and year", description = "Retrieves all payslips for a specific month and year")
    @GetMapping("/month/{month}/year/{year}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<PayslipResponse>> getPayslipsByMonthAndYear(
            @PathVariable Integer month,
            @PathVariable Integer year) {
        return ResponseEntity.ok(payslipService.getPayslipsByMonthAndYear(month, year));
    }

    @Operation(summary = "Approve payslips", description = "Approves all payslips for a specific month and year")
    @PostMapping("/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> approvePayslips(
            @RequestParam Integer month,
            @RequestParam Integer year) {
        payslipService.approvePayslips(month, year);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Download payslip", description = "Downloads a payslip as a PDF file")
    @GetMapping("/{payslipId}/download")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or @securityService.isCurrentUser(#payslipId)")
    public ResponseEntity<byte[]> downloadPayslip(@PathVariable Long payslipId) {
        byte[] pdfBytes = payslipService.generatePayslipPdf(payslipId);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=payslip.pdf")
                .body(pdfBytes);
    }
} 