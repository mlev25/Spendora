package com.spendora.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalendarDayDTO {
    private LocalDate date;
    private BigDecimal totalAmount;
    private Integer expenseCount;
    private List<ExpenseDTO> expenses;
}
