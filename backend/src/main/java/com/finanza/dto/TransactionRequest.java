package com.finanza.dto;

import com.finanza.model.Transaction;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {
    
    @NotNull(message = "El tipo de transacción es requerido")
    private Transaction.TransactionType type;
    
    @NotNull(message = "El monto es requerido")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
    private BigDecimal amount;
    
    @NotNull(message = "La fecha de transacción es requerida")
    private LocalDate transactionDate;
    
    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String description;
    
    @Size(max = 1000, message = "Las notas no pueden exceder 1000 caracteres")
    private String notes;
    
    @NotNull(message = "La cuenta es requerida")
    private Long accountId;
    
    private Long categoryId;
    
    // Solo para transferencias
    private Long destinationAccountId;
    
    private Set<String> tags;
    
    private Set<String> attachments;
}
