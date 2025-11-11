package com.finanza.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRateResponse {
    private Long id;
    private LocalDate date;
    private String rateType;
    private BigDecimal buyRate;
    private BigDecimal sellRate;
    private BigDecimal averageRate;
}
