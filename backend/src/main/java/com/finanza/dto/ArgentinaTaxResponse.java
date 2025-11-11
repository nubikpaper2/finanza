package com.finanza.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArgentinaTaxResponse {
    private Long id;
    private Long transactionId;
    private String taxType;
    private BigDecimal percentage;
    private BigDecimal amount;
    private String description;
}
