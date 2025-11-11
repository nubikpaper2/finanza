package com.finanza.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportTransactionRequest {
    private LocalDate date;
    private String description;
    private BigDecimal amount;
    private String type; // INCOME, EXPENSE
    private String category;
    private String notes;
    private Long accountId;
}
