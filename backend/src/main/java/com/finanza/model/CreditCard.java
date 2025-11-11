package com.finanza.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "credit_cards")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 4)
    private String lastFourDigits;

    @Column(nullable = false)
    private Integer closingDay; // Día de cierre (1-31)

    @Column(nullable = false)
    private Integer dueDay; // Día de vencimiento (1-31)

    @Column(precision = 15, scale = 2)
    private BigDecimal creditLimit;

    @Column(length = 10)
    private String currency = "ARS";

    @Column(length = 50)
    private String bank;

    @Column(nullable = false)
    private Boolean active = true;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public CreditCard(String name, Integer closingDay, Integer dueDay, 
                     String currency, Organization organization) {
        this.name = name;
        this.closingDay = closingDay;
        this.dueDay = dueDay;
        this.currency = currency;
        this.organization = organization;
    }
}
