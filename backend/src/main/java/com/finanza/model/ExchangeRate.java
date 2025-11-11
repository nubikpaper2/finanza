package com.finanza.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "exchange_rates", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"date", "rate_type", "organization_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(name = "rate_type", nullable = false, length = 20)
    private RateType rateType;

    @Column(nullable = false, precision = 15, scale = 4)
    private BigDecimal buyRate;

    @Column(nullable = false, precision = 15, scale = 4)
    private BigDecimal sellRate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum RateType {
        OFICIAL,    // Dólar oficial
        MEP,        // Dólar MEP (Bolsa)
        BLUE,       // Dólar blue
        TARJETA     // Dólar tarjeta (oficial + impuestos)
    }

    public ExchangeRate(LocalDate date, RateType rateType, BigDecimal buyRate, 
                       BigDecimal sellRate, Organization organization) {
        this.date = date;
        this.rateType = rateType;
        this.buyRate = buyRate;
        this.sellRate = sellRate;
        this.organization = organization;
    }

    // Método de conveniencia para obtener la tasa promedio
    public BigDecimal getAverageRate() {
        return buyRate.add(sellRate).divide(BigDecimal.valueOf(2));
    }
}
