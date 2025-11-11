package com.finanza.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArgentinaTaxRequest {
    private String taxType; // PAIS, PERCEPCION_RG_5371, etc.
    private BigDecimal percentage;
    private BigDecimal amount;
    private String description;
}
