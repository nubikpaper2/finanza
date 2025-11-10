package com.finanza.dto;

import com.finanza.model.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequest {
    
    @NotBlank(message = "El nombre es requerido")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String name;
    
    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String description;
    
    @NotNull(message = "El tipo es requerido")
    private Category.TransactionType type;
    
    @Size(max = 50, message = "El icono no puede exceder 50 caracteres")
    private String icon;
    
    @Size(max = 20, message = "El color no puede exceder 20 caracteres")
    private String color;
    
    private Boolean active = true;
}
