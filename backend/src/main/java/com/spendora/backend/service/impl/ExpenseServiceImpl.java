package com.spendora.backend.service.impl;

import com.spendora.backend.dto.category.CategorySpendingDTO;
import com.spendora.backend.dto.expense.ExpenseDTO;
import com.spendora.backend.dto.statistics.CalendarDayDTO;
import com.spendora.backend.dto.statistics.ExpenseSummaryDTO;
import com.spendora.backend.dto.statistics.MonthlyStatisticsDTO;
import com.spendora.backend.dto.statistics.YearlyStatisticsDTO;
import com.spendora.backend.entity.Category;
import com.spendora.backend.entity.Expense;
import com.spendora.backend.entity.User;
import com.spendora.backend.repository.CategoryRepository;
import com.spendora.backend.repository.ExpenseRepository;
import com.spendora.backend.repository.UserRepository;
import com.spendora.backend.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public ExpenseDTO addExpense(ExpenseDTO expenseDTO) {
        Optional<User> userOpt = userRepository.findById(expenseDTO.getUserId());
        Optional<Category> categoryOpt = categoryRepository.findById(expenseDTO.getCategoryId());

        if (userOpt.isEmpty() || categoryOpt.isEmpty()) {
            throw new IllegalArgumentException("User or Category not found");
        }

        Expense expense = new Expense();
        expense.setName(expenseDTO.getName());
        expense.setPrice(expenseDTO.getPrice());
        expense.setDescription(expenseDTO.getDescription());
        expense.setCategory(categoryOpt.get());
        expense.setUser(userOpt.get());
        expense.setDate(expenseDTO.getDate());

        Expense saved = expenseRepository.save(expense);
        return toDTO(saved);
    }

    @Override
    public List<ExpenseDTO> getAllExpenses(Long userId) {
        return expenseRepository.findByUserId(userId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ExpenseDTO> updateExpense(Long id, ExpenseDTO expenseDTO) {
        return expenseRepository.findById(id).map(existing -> {
            existing.setName(expenseDTO.getName());
            existing.setDescription(expenseDTO.getDescription());
            existing.setPrice(expenseDTO.getPrice());
            existing.setDate(expenseDTO.getDate());

            Category category = categoryRepository.findById(expenseDTO.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found"));
            existing.setCategory(category);

            Expense updated = expenseRepository.save(existing);
            return toDTO(updated);
        });
    }

    @Override
    public void deleteExpense(Long id) {
        if (!expenseRepository.existsById(id)) {
            throw new IllegalArgumentException("Expense not found");
        }
        expenseRepository.deleteById(id);

    }

    @Override
    public Optional<ExpenseDTO> findById(Long id) {
        return expenseRepository.findById(id)
                .map(this::toDTO);
    }

    @Override
    public List<ExpenseDTO> listAllExpensesByUser(Long userId) {
        return getAllExpenses(userId);
    }

    //toDTO and fromDTO implementation
    private ExpenseDTO toDTO(Expense expense) {
        ExpenseDTO dto = new ExpenseDTO();
        dto.setId(expense.getId());
        dto.setName(expense.getName());
        dto.setDescription(expense.getDescription());
        dto.setPrice(expense.getPrice());
        dto.setDate(expense.getDate());
        dto.setUserId(expense.getUser().getId());
        dto.setCategoryId(expense.getCategory().getId());
        // Add category details for display
        dto.setCategoryName(expense.getCategory().getName());
        dto.setCategoryColor(expense.getCategory().getColor());
        dto.setCategoryIcon(expense.getCategory().getIcon());
        return dto;
    }

    private Expense fromDTO(ExpenseDTO dto, User user, Category category) {
        Expense expense = new Expense();
        expense.setName(dto.getName());
        expense.setDescription(dto.getDescription());
        expense.setPrice(dto.getPrice());
        expense.setDate(dto.getDate());
        expense.setUser(user);
        expense.setCategory(category);
        return expense;
    }

    @Override
    public MonthlyStatisticsDTO getMonthlyStatistics(Long userId, Integer year, Integer month) {
        BigDecimal totalAmount = expenseRepository.sumByUserAndYearAndMonth(userId, year, month);
        Long expenseCount = expenseRepository.countByUserAndYearAndMonth(userId, year, month);
        
        if (totalAmount == null) totalAmount = BigDecimal.ZERO;
        if (expenseCount == null) expenseCount = 0L;
        
        BigDecimal averageExpense = expenseCount > 0 
            ? totalAmount.divide(BigDecimal.valueOf(expenseCount), 2, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;

        // Get category breakdown for the month
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        List<CategorySpendingDTO> categoryBreakdown = getCategorySpending(userId, startDate, endDate);

        return MonthlyStatisticsDTO.builder()
                .year(year)
                .month(month)
                .totalAmount(totalAmount)
                .expenseCount(expenseCount)
                .averageExpense(averageExpense)
                .categoryBreakdown(categoryBreakdown)
                .build();
    }

    @Override
    public YearlyStatisticsDTO getYearlyStatistics(Long userId, Integer year) {
        BigDecimal totalAmount = expenseRepository.sumByUserAndYear(userId, year);
        if (totalAmount == null) totalAmount = BigDecimal.ZERO;

        List<Object[]> monthlyData = expenseRepository.monthlyBreakdownByYear(userId, year);
        Map<Integer, BigDecimal> monthlyBreakdown = new HashMap<>();
        
        // Initialize all months with 0
        for (int i = 1; i <= 12; i++) {
            monthlyBreakdown.put(i, BigDecimal.ZERO);
        }
        
        // Fill in actual data
        for (Object[] row : monthlyData) {
            Integer month = (Integer) row[0];
            BigDecimal amount = (BigDecimal) row[1];
            monthlyBreakdown.put(month, amount);
        }

        Long expenseCount = expenseRepository.countAllByUser(userId);
        BigDecimal averageMonthly = totalAmount.divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);

        return YearlyStatisticsDTO.builder()
                .year(year)
                .totalAmount(totalAmount)
                .expenseCount(expenseCount)
                .averageMonthlyExpense(averageMonthly)
                .monthlyBreakdown(monthlyBreakdown)
                .build();
    }

    @Override
    public List<CategorySpendingDTO> getCategorySpending(Long userId, LocalDate startDate, LocalDate endDate) {
        List<Object[]> results = expenseRepository.categorySpendingByDateRange(userId, startDate, endDate);
        
        // Calculate total for percentage
        BigDecimal total = results.stream()
                .map(row -> (BigDecimal) row[4])
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return results.stream().map(row -> {
            Long categoryId = (Long) row[0];
            String categoryName = (String) row[1];
            String categoryColor = (String) row[2];
            String categoryIcon = (String) row[3];
            BigDecimal amount = (BigDecimal) row[4];
            Long count = (Long) row[5];
            
            Double percentage = total.compareTo(BigDecimal.ZERO) > 0
                ? amount.divide(total, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue()
                : 0.0;

            return CategorySpendingDTO.builder()
                    .categoryId(categoryId)
                    .categoryName(categoryName)
                    .categoryColor(categoryColor)
                    .categoryIcon(categoryIcon)
                    .totalAmount(amount)
                    .expenseCount(count)
                    .percentage(percentage)
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    public ExpenseSummaryDTO getExpenseSummary(Long userId) {
        BigDecimal totalExpenses = expenseRepository.sumAllByUser(userId);
        Long expenseCount = expenseRepository.countAllByUser(userId);
        BigDecimal averageExpense = expenseRepository.avgByUser(userId);
        BigDecimal highestExpense = expenseRepository.maxByUser(userId);
        BigDecimal lowestExpense = expenseRepository.minByUser(userId);

        // This month
        LocalDate now = LocalDate.now();
        YearMonth thisMonth = YearMonth.from(now);
        BigDecimal thisMonthTotal = expenseRepository.sumByUserAndYearAndMonth(
            userId, thisMonth.getYear(), thisMonth.getMonthValue());

        // Last month
        YearMonth lastMonth = thisMonth.minusMonths(1);
        BigDecimal lastMonthTotal = expenseRepository.sumByUserAndYearAndMonth(
            userId, lastMonth.getYear(), lastMonth.getMonthValue());

        // This year
        BigDecimal thisYearTotal = expenseRepository.sumByUserAndYear(userId, now.getYear());

        // Handle nulls
        if (totalExpenses == null) totalExpenses = BigDecimal.ZERO;
        if (averageExpense == null) averageExpense = BigDecimal.ZERO;
        if (highestExpense == null) highestExpense = BigDecimal.ZERO;
        if (lowestExpense == null) lowestExpense = BigDecimal.ZERO;
        if (thisMonthTotal == null) thisMonthTotal = BigDecimal.ZERO;
        if (lastMonthTotal == null) lastMonthTotal = BigDecimal.ZERO;
        if (thisYearTotal == null) thisYearTotal = BigDecimal.ZERO;

        return ExpenseSummaryDTO.builder()
                .totalExpenses(totalExpenses)
                .expenseCount(expenseCount)
                .averageExpense(averageExpense)
                .thisMonthTotal(thisMonthTotal)
                .lastMonthTotal(lastMonthTotal)
                .thisYearTotal(thisYearTotal)
                .highestExpense(highestExpense)
                .lowestExpense(lowestExpense)
                .build();
    }

    @Override
    public List<CalendarDayDTO> getCalendarData(Long userId, Integer year, Integer month) {
        // Get all expenses for the given month
        List<Expense> expenses = expenseRepository.findByUserIdAndYearAndMonth(userId, year, month);
        
        // Group by date
        Map<LocalDate, List<Expense>> groupedByDate = expenses.stream()
                .collect(Collectors.groupingBy(Expense::getDate));
        
        // Convert to CalendarDayDTO list
        return groupedByDate.entrySet().stream()
                .map(entry -> {
                    LocalDate date = entry.getKey();
                    List<Expense> dayExpenses = entry.getValue();
                    
                    BigDecimal totalAmount = dayExpenses.stream()
                            .map(Expense::getPrice)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    
                    List<ExpenseDTO> expenseDTOs = dayExpenses.stream()
                            .map(this::toDTO)
                            .collect(Collectors.toList());
                    
                    return CalendarDayDTO.builder()
                            .date(date)
                            .totalAmount(totalAmount)
                            .expenseCount(dayExpenses.size())
                            .expenses(expenseDTOs)
                            .build();
                })
                .sorted(Comparator.comparing(CalendarDayDTO::getDate))
                .collect(Collectors.toList());
    }

}
