package com.finanza.controller;

import com.finanza.dto.CategoryRuleRequest;
import com.finanza.dto.CategoryRuleResponse;
import com.finanza.model.User;
import com.finanza.service.CategoryRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category-rules")
@RequiredArgsConstructor
public class CategoryRuleController {

    private final CategoryRuleService categoryRuleService;

    @PostMapping
    public ResponseEntity<CategoryRuleResponse> createRule(
            @RequestBody CategoryRuleRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(categoryRuleService.createRule(request, user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryRuleResponse> updateRule(
            @PathVariable Long id,
            @RequestBody CategoryRuleRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(categoryRuleService.updateRule(id, request, user));
    }

    @GetMapping
    public ResponseEntity<List<CategoryRuleResponse>> getAllRules(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(categoryRuleService.getAllRules(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryRuleResponse> getRuleById(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(categoryRuleService.getRuleById(id, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRule(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        categoryRuleService.deleteRule(id, user);
        return ResponseEntity.noContent().build();
    }
}
