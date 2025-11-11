package com.finanza.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstallmentResponse {
    private Long id;
    private Long transactionId;
    private String transactionDescription;
    private Long creditCardId;
    private String creditCardName;
    private Integer installmentNumber;
    private Integer totalInstallments;
    private BigDecimal amount;
    private LocalDate dueDate;
    private Boolean paid;
    private LocalDate paidDate;
}
