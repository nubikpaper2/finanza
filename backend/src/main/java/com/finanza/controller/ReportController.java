package com.finanza.controller;

import com.finanza.dto.MonthlyReportResponse;
import com.finanza.model.User;
import com.finanza.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/monthly/{year}/{month}")
    public ResponseEntity<MonthlyReportResponse> getMonthlyReport(
            @PathVariable Integer year,
            @PathVariable Integer month,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(reportService.getMonthlyReport(year, month, user));
    }

    @GetMapping("/yearly/{year}")
    public ResponseEntity<List<MonthlyReportResponse>> getYearlyReport(
            @PathVariable Integer year,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(reportService.getYearlyReport(year, user));
    }
}
