package com.finanza.controller;

import com.finanza.dto.InstallmentResponse;
import com.finanza.model.Organization;
import com.finanza.model.User;
import com.finanza.service.CreditCardInstallmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/installments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class InstallmentController {

    private final CreditCardInstallmentService installmentService;

    @GetMapping("/upcoming")
    public ResponseEntity<List<InstallmentResponse>> getUpcoming(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @AuthenticationPrincipal User user) {
        Organization org = user.getOrganization();
        List<InstallmentResponse> installments = installmentService.getUpcomingInstallments(
            org, startDate, endDate);
        return ResponseEntity.ok(installments);
    }

    @GetMapping("/unpaid")
    public ResponseEntity<List<InstallmentResponse>> getUnpaid(
            @AuthenticationPrincipal User user) {
        Organization org = user.getOrganization();
        List<InstallmentResponse> installments = installmentService.getUnpaidInstallments(org);
        return ResponseEntity.ok(installments);
    }

    @GetMapping("/credit-card/{creditCardId}")
    public ResponseEntity<List<InstallmentResponse>> getByCreditCard(
            @PathVariable Long creditCardId,
            @AuthenticationPrincipal User user) {
        Organization org = user.getOrganization();
        List<InstallmentResponse> installments = installmentService
            .getInstallmentsByCreditCard(creditCardId, org);
        return ResponseEntity.ok(installments);
    }

    @PatchMapping("/{id}/pay")
    public ResponseEntity<InstallmentResponse> markAsPaid(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        Organization org = user.getOrganization();
        InstallmentResponse installment = installmentService.markAsPaid(id, org);
        return ResponseEntity.ok(installment);
    }

    @PatchMapping("/{id}/unpay")
    public ResponseEntity<InstallmentResponse> markAsUnpaid(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        Organization org = user.getOrganization();
        InstallmentResponse installment = installmentService.markAsUnpaid(id, org);
        return ResponseEntity.ok(installment);
    }
}
