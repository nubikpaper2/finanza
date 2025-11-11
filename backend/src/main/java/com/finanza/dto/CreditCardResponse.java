package com.finanza.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditCardResponse {
    private Long id;
    private String name;
    private String lastFourDigits;
    private Integer closingDay;
    private Integer dueDay;
    private BigDecimal creditLimit;
    private String currency;
    private String bank;
    private Boolean active;
    private Long accountId;
    private String accountName;
    private BigDecimal currentDebt; // Deuda actual (cuotas impagas)
    private BigDecimal availableCredit; // Crédito disponible
}
