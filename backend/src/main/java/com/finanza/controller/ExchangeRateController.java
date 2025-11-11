package com.finanza.controller;

import com.finanza.dto.ExchangeRateRequest;
import com.finanza.dto.ExchangeRateResponse;
import com.finanza.model.Organization;
import com.finanza.model.User;
import com.finanza.service.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/exchange-rates")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;

    @PostMapping
    public ResponseEntity<ExchangeRateResponse> createOrUpdateRate(
            @RequestBody ExchangeRateRequest request,
            @AuthenticationPrincipal User user) {
        Organization org = user.getOrganization();
        ExchangeRateResponse response = exchangeRateService.createOrUpdate(request, org);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<ExchangeRateResponse>> getRatesByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @AuthenticationPrincipal User user) {
        Organization org = user.getOrganization();
        List<ExchangeRateResponse> rates = exchangeRateService.getRatesByDate(date, org);
        return ResponseEntity.ok(rates);
    }

    @GetMapping("/range")
    public ResponseEntity<List<ExchangeRateResponse>> getRatesByRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam String rateType,
            @AuthenticationPrincipal User user) {
        Organization org = user.getOrganization();
        List<ExchangeRateResponse> rates = exchangeRateService.getRatesByDateRange(
            startDate, endDate, rateType, org);
        return ResponseEntity.ok(rates);
    }

    @GetMapping("/latest/{rateType}")
    public ResponseEntity<ExchangeRateResponse> getLatestRate(
            @PathVariable String rateType,
            @AuthenticationPrincipal User user) {
        Organization org = user.getOrganization();
        ExchangeRateResponse rate = exchangeRateService.getLatestRate(rateType, org);
        return ResponseEntity.ok(rate);
    }

    @GetMapping
    public ResponseEntity<List<ExchangeRateResponse>> getAllRates(
            @AuthenticationPrincipal User user) {
        Organization org = user.getOrganization();
        List<ExchangeRateResponse> rates = exchangeRateService.getAllRates(org);
        return ResponseEntity.ok(rates);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRate(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        Organization org = user.getOrganization();
        exchangeRateService.deleteRate(id, org);
        return ResponseEntity.noContent().build();
    }
}
