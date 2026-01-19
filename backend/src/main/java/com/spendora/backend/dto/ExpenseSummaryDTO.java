package com.spendora.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseSummaryDTO {
    private BigDecimal totalExpenses;
    private Long expenseCount;
    private BigDecimal averageExpense;
    private BigDecimal thisMonthTotal;
    private BigDecimal lastMonthTotal;
    private BigDecimal thisYearTotal;
    private BigDecimal highestExpense;
    private BigDecimal lowestExpense;
}
