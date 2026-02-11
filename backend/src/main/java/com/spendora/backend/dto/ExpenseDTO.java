package com.spendora.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseDTO {
    private Long id;

    @NotBlank(message = "Name must not be blank")
    private String name;

    @Size(max = 255, message = "Description can be at most 200 characters")
    private String description;

    @NotNull(message = "Price must not be null")
    @Positive(message = "Price must be positive")
    private BigDecimal price;

    @NotNull(message = "Date must not be null")
    private LocalDate date;

    // userId is set by the controller from JWT token, not from request
    private Long userId;

    @NotNull(message = "Category ID must not be null")
    private Long categoryId;

}
