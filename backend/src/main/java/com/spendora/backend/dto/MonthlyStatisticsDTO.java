package com.spendora.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyStatisticsDTO {
    private Integer year;
    private Integer month;
    private BigDecimal totalAmount;
    private Long expenseCount;
    private BigDecimal averageExpense;
    private List<CategorySpendingDTO> categoryBreakdown;
}
