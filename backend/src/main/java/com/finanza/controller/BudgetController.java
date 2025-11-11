package com.finanza.controller;

import com.finanza.dto.BudgetRequest;
import com.finanza.dto.BudgetResponse;
import com.finanza.model.User;
import com.finanza.service.BudgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    @PostMapping
    public ResponseEntity<BudgetResponse> createBudget(
            @RequestBody BudgetRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(budgetService.createBudget(request, user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BudgetResponse> updateBudget(
            @PathVariable Long id,
            @RequestBody BudgetRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(budgetService.updateBudget(id, request, user));
    }

    @GetMapping("/month/{year}/{month}")
    public ResponseEntity<List<BudgetResponse>> getBudgetsByMonth(
            @PathVariable Integer year,
            @PathVariable Integer month,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(budgetService.getBudgetsByMonth(year, month, user));
    }

    @GetMapping("/year/{year}")
    public ResponseEntity<List<BudgetResponse>> getBudgetsByYear(
            @PathVariable Integer year,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(budgetService.getBudgetsByYear(year, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBudget(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        budgetService.deleteBudget(id, user);
        return ResponseEntity.noContent().build();
    }
}
