package com.spendora.backend.service;

import com.spendora.backend.dto.category.CategorySpendingDTO;
import com.spendora.backend.dto.expense.ExpenseDTO;
import com.spendora.backend.dto.statistics.CalendarDayDTO;
import com.spendora.backend.dto.statistics.ExpenseSummaryDTO;
import com.spendora.backend.dto.statistics.MonthlyStatisticsDTO;
import com.spendora.backend.dto.statistics.YearlyStatisticsDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExpenseService {
    ExpenseDTO addExpense(ExpenseDTO expenseDTO);
    List<ExpenseDTO> getAllExpenses(Long userId);
    Optional<ExpenseDTO> updateExpense(Long id, ExpenseDTO expenseDTO);
    void deleteExpense(Long id);
    Optional<ExpenseDTO> findById(Long id);
    List<ExpenseDTO> listAllExpensesByUser(Long userId);
    
    // Statistics methods
    MonthlyStatisticsDTO getMonthlyStatistics(Long userId, Integer year, Integer month);
    YearlyStatisticsDTO getYearlyStatistics(Long userId, Integer year);
    List<CategorySpendingDTO> getCategorySpending(Long userId, LocalDate startDate, LocalDate endDate);
    ExpenseSummaryDTO getExpenseSummary(Long userId);
    List<CalendarDayDTO> getCalendarData(Long userId, Integer year, Integer month);
}
