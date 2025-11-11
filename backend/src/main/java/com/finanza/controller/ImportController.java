package com.finanza.controller;

import com.finanza.dto.ImportResponse;
import com.finanza.dto.ImportTransactionRequest;
import com.finanza.model.User;
import com.finanza.service.ImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/import")
@RequiredArgsConstructor
public class ImportController {

    private final ImportService importService;

    @PostMapping("/csv")
    public ResponseEntity<ImportResponse> importCSV(
            @RequestParam("file") MultipartFile file,
            @RequestParam("accountId") Long accountId,
            @AuthenticationPrincipal User user) {
        try {
            ImportResponse response = importService.importCSV(file, accountId, user);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new ImportResponse(0, 0, 0, List.of(e.getMessage()), List.of())
            );
        }
    }

    @PostMapping("/excel")
    public ResponseEntity<ImportResponse> importExcel(
            @RequestParam("file") MultipartFile file,
            @RequestParam("accountId") Long accountId,
            @AuthenticationPrincipal User user) {
        try {
            ImportResponse response = importService.importExcel(file, accountId, user);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new ImportResponse(0, 0, 0, List.of(e.getMessage()), List.of())
            );
        }
    }

    @PostMapping("/json")
    public ResponseEntity<ImportResponse> importJSON(
            @RequestBody List<ImportTransactionRequest> requests,
            @AuthenticationPrincipal User user) {
        try {
            ImportResponse response = importService.importTransactions(requests, user);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new ImportResponse(0, 0, 0, List.of(e.getMessage()), List.of())
            );
        }
    }
}
