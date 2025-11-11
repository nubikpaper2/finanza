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
@Table(name = "credit_card_installments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditCardInstallment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false)
    private Transaction transaction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credit_card_id", nullable = false)
    private CreditCard creditCard;

    @Column(nullable = false)
    private Integer installmentNumber; // Número de cuota actual

    @Column(nullable = false)
    private Integer totalInstallments; // Total de cuotas

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount; // Monto de esta cuota

    @Column(nullable = false)
    private LocalDate dueDate; // Fecha de vencimiento de esta cuota

    @Column(nullable = false)
    private Boolean paid = false;

    @Column
    private LocalDate paidDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public CreditCardInstallment(Transaction transaction, CreditCard creditCard, 
                                Integer installmentNumber, Integer totalInstallments,
                                BigDecimal amount, LocalDate dueDate, 
                                Organization organization) {
        this.transaction = transaction;
        this.creditCard = creditCard;
        this.installmentNumber = installmentNumber;
        this.totalInstallments = totalInstallments;
        this.amount = amount;
        this.dueDate = dueDate;
        this.organization = organization;
    }
}
