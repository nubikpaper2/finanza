package com.finanza.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRateRequest {
    private LocalDate date;
    private String rateType; // OFICIAL, MEP, BLUE, TARJETA
    private BigDecimal buyRate;
    private BigDecimal sellRate;
}
