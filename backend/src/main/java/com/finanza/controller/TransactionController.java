package com.finanza.controller;

import com.finanza.dto.TransactionRequest;
import com.finanza.dto.TransactionResponse;
import com.finanza.dto.TransferRequest;
import com.finanza.model.Organization;
import com.finanza.model.Transaction;
import com.finanza.model.User;
import com.finanza.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<Page<TransactionResponse>> getAllTransactions(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Transaction.TransactionType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "transactionDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Organization organization = new Organization();
        organization.setId(1L);
        
        String direction = sortDirection != null ? sortDirection : "DESC";
        Sort.Direction sortDir = Sort.Direction.fromString(direction);
        Sort sort = Sort.by(sortDir, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<TransactionResponse> transactions;
        
        if (startDate != null && endDate != null) {
            transactions = transactionService.getTransactionsByFilters(
                organization, startDate, endDate, categoryId, type, pageable);
        } else {
            transactions = transactionService.getAllTransactions(organization, pageable);
        }
        
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransactionById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Organization organization = new Organization();
        organization.setId(1L);
        
        TransactionResponse transaction = transactionService.getTransactionById(id, organization);
        return ResponseEntity.ok(transaction);
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(
            @Valid @RequestBody TransactionRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Organization organization = new Organization();
        organization.setId(1L);
        
        User user = new User();
        user.setId(1L);
        
        TransactionResponse transaction = transactionService.createTransaction(request, organization, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponse> updateTransaction(
            @PathVariable Long id,
            @Valid @RequestBody TransactionRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Organization organization = new Organization();
        organization.setId(1L);
        
        TransactionResponse transaction = transactionService.updateTransaction(id, request, organization);
        return ResponseEntity.ok(transaction);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Organization organization = new Organization();
        organization.setId(1L);
        
        transactionService.deleteTransaction(id, organization);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> createTransfer(
            @Valid @RequestBody TransferRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Organization organization = new Organization();
        organization.setId(1L);
        
        User user = new User();
        user.setId(1L);
        
        TransactionResponse transaction = transactionService.createTransfer(request, organization, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
    }
}
