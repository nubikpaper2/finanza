package com.finanza.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequest {
    
    @NotNull(message = "La cuenta origen es requerida")
    private Long fromAccountId;
    
    @NotNull(message = "La cuenta destino es requerida")
    private Long toAccountId;
    
    @NotNull(message = "El monto es requerido")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
    private BigDecimal amount;
    
    @NotNull(message = "La fecha de transferencia es requerida")
    private LocalDate transactionDate;
    
    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String description;
    
    @Size(max = 1000, message = "Las notas no pueden exceder 1000 caracteres")
    private String notes;
}
