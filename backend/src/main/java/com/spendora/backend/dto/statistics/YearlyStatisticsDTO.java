package com.spendora.backend.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class YearlyStatisticsDTO {
    private Integer year;
    private BigDecimal totalAmount;
    private Long expenseCount;
    private BigDecimal averageMonthlyExpense;
    private Map<Integer, BigDecimal> monthlyBreakdown; // month (1-12) -> amount
}
