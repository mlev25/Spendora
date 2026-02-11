package com.spendora.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PredictCategoryRequest {
    
    @NotBlank(message = "Expense name is required")
    private String name;
    
    private BigDecimal price;
    
    private String description;
}
