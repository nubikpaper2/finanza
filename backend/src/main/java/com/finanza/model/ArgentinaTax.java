package com.finanza.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "argentina_taxes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArgentinaTax {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false)
    private Transaction transaction;

    @Enumerated(EnumType.STRING)
    @Column(name = "tax_type", nullable = false, length = 30)
    private TaxType taxType;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal percentage; // Porcentaje del impuesto

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount; // Monto del impuesto

    @Column(length = 500)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum TaxType {
        PAIS,           // Impuesto PAIS (30%)
        PERCEPCION_RG_5371,  // Percepción RG 5371 (45%)
        PERCEPCION_RG_4815,  // Percepción RG 4815 (35%)
        IVA,            // IVA (21%)
        IIBB,           // Ingresos Brutos
        OTROS           // Otros impuestos
    }

    public ArgentinaTax(Transaction transaction, TaxType taxType, 
                       BigDecimal percentage, BigDecimal amount, 
                       Organization organization) {
        this.transaction = transaction;
        this.taxType = taxType;
        this.percentage = percentage;
        this.amount = amount;
        this.organization = organization;
    }

    // Constructor con descripción
    public ArgentinaTax(Transaction transaction, TaxType taxType, 
                       BigDecimal percentage, BigDecimal amount, 
                       String description, Organization organization) {
        this(transaction, taxType, percentage, amount, organization);
        this.description = description;
    }
}
