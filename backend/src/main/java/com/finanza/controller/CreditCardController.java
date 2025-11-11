package com.finanza.controller;

import com.finanza.dto.CreditCardRequest;
import com.finanza.dto.CreditCardResponse;
import com.finanza.model.Organization;
import com.finanza.model.User;
import com.finanza.service.CreditCardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/credit-cards")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CreditCardController {

    private final CreditCardService creditCardService;

    @PostMapping
    public ResponseEntity<CreditCardResponse> create(
            @Valid @RequestBody CreditCardRequest request,
            @AuthenticationPrincipal User user) {
        Organization org = user.getOrganization();
        CreditCardResponse response = creditCardService.create(request, org, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CreditCardResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody CreditCardRequest request,
            @AuthenticationPrincipal User user) {
        Organization org = user.getOrganization();
        CreditCardResponse response = creditCardService.update(id, request, org);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<CreditCardResponse>> getAll(
            @RequestParam(required = false) Boolean activeOnly,
            @AuthenticationPrincipal User user) {
        Organization org = user.getOrganization();
        List<CreditCardResponse> cards = activeOnly != null && activeOnly ?
            creditCardService.getActive(org) : creditCardService.getAll(org);
        return ResponseEntity.ok(cards);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CreditCardResponse> getById(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        Organization org = user.getOrganization();
        CreditCardResponse card = creditCardService.getById(id, org);
        return ResponseEntity.ok(card);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        Organization org = user.getOrganization();
        creditCardService.delete(id, org);
        return ResponseEntity.noContent().build();
    }
}
