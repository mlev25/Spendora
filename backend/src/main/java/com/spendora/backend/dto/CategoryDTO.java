package com.spendora.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
    private Long id;
    
    @NotBlank(message = "Category name must not be blank")
    @Size(max = 50, message = "Category name can be at most 50 characters")
    private String name;
    
    private String color;
    
    private String icon;
}
