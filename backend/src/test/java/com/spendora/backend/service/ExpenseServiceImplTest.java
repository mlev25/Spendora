package com.spendora.backend.service;

import com.spendora.backend.dto.expense.ExpenseDTO;
import com.spendora.backend.dto.statistics.ExpenseSummaryDTO;
import com.spendora.backend.dto.statistics.MonthlyStatisticsDTO;
import com.spendora.backend.entity.Category;
import com.spendora.backend.entity.Expense;
import com.spendora.backend.entity.User;
import com.spendora.backend.repository.CategoryRepository;
import com.spendora.backend.repository.ExpenseRepository;
import com.spendora.backend.repository.UserRepository;
import com.spendora.backend.service.impl.ExpenseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ExpenseServiceImpl
 * Tests CRUD operations, statistics calculations, and business logic
 */
@ExtendWith(MockitoExtension.class)
class ExpenseServiceImplTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ExpenseServiceImpl expenseService;

    private User testUser;
    private Category testCategory;
    private Expense testExpense;
    private ExpenseDTO testExpenseDTO;

    @BeforeEach
    void setUp() {
        // Setup test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");

        // Setup test category
        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Food");
        testCategory.setColor("#FF5733");
        testCategory.setIcon("🍔");

        // Setup test expense
        testExpense = new Expense();
        testExpense.setId(1L);
        testExpense.setName("Lunch");
        testExpense.setDescription("Pizza");
        testExpense.setPrice(new BigDecimal("2500.00"));
        testExpense.setDate(LocalDate.of(2026, 3, 4));
        testExpense.setUser(testUser);
        testExpense.setCategory(testCategory);

        // Setup test DTO
        testExpenseDTO = new ExpenseDTO();
        testExpenseDTO.setName("Lunch");
        testExpenseDTO.setDescription("Pizza");
        testExpenseDTO.setPrice(new BigDecimal("2500.00"));
        testExpenseDTO.setDate(LocalDate.of(2026, 3, 4));
        testExpenseDTO.setUserId(1L);
        testExpenseDTO.setCategoryId(1L);
    }

    @Test
    void testAddExpense_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(expenseRepository.save(any(Expense.class))).thenReturn(testExpense);

        // When
        ExpenseDTO result = expenseService.addExpense(testExpenseDTO);

        // Then
        assertNotNull(result);
        assertEquals("Lunch", result.getName());
        assertEquals(new BigDecimal("2500.00"), result.getPrice());
        assertEquals("Food", result.getCategoryName());
        verify(expenseRepository, times(1)).save(any(Expense.class));
    }

    @Test
    void testAddExpense_UserNotFound_ThrowsException() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> expenseService.addExpense(testExpenseDTO)
        );
        assertEquals("User or Category not found", exception.getMessage());
        verify(expenseRepository, never()).save(any());
    }

    @Test
    void testAddExpense_CategoryNotFound_ThrowsException() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> expenseService.addExpense(testExpenseDTO)
        );
        assertEquals("User or Category not found", exception.getMessage());
        verify(expenseRepository, never()).save(any());
    }

    @Test
    void testGetAllExpenses_ReturnsExpenseList() {
        // Given
        Expense expense2 = new Expense();
        expense2.setId(2L);
        expense2.setName("Dinner");
        expense2.setPrice(new BigDecimal("3500.00"));
        expense2.setDate(LocalDate.of(2026, 3, 5));
        expense2.setUser(testUser);
        expense2.setCategory(testCategory);

        List<Expense> expenses = Arrays.asList(testExpense, expense2);
        when(expenseRepository.findByUserId(1L)).thenReturn(expenses);

        // When
        List<ExpenseDTO> result = expenseService.getAllExpenses(1L);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Lunch", result.get(0).getName());
        assertEquals("Dinner", result.get(1).getName());
        verify(expenseRepository, times(1)).findByUserId(1L);
    }

    @Test
    void testUpdateExpense_Success() {
        // Given
        ExpenseDTO updateDTO = new ExpenseDTO();
        updateDTO.setName("Updated Lunch");
        updateDTO.setDescription("Updated Pizza");
        updateDTO.setPrice(new BigDecimal("3000.00"));
        updateDTO.setDate(LocalDate.of(2026, 3, 5));
        updateDTO.setCategoryId(1L);

        when(expenseRepository.findById(1L)).thenReturn(Optional.of(testExpense));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(expenseRepository.save(any(Expense.class))).thenReturn(testExpense);

        // When
        Optional<ExpenseDTO> result = expenseService.updateExpense(1L, updateDTO);

        // Then
        assertTrue(result.isPresent());
        verify(expenseRepository, times(1)).save(any(Expense.class));
    }

    @Test
    void testUpdateExpense_ExpenseNotFound_ReturnsEmpty() {
        // Given
        when(expenseRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        Optional<ExpenseDTO> result = expenseService.updateExpense(1L, testExpenseDTO);

        // Then
        assertFalse(result.isPresent());
        verify(expenseRepository, never()).save(any());
    }

    @Test
    void testDeleteExpense_Success() {
        // Given
        when(expenseRepository.existsById(1L)).thenReturn(true);
        doNothing().when(expenseRepository).deleteById(1L);

        // When
        expenseService.deleteExpense(1L);

        // Then
        verify(expenseRepository, times(1)).existsById(1L);
        verify(expenseRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteExpense_NotFound_ThrowsException() {
        // Given
        when(expenseRepository.existsById(1L)).thenReturn(false);

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> expenseService.deleteExpense(1L)
        );
        assertEquals("Expense not found", exception.getMessage());
        verify(expenseRepository, never()).deleteById(anyLong());
    }

    @Test
    void testFindById_Success() {
        // Given
        when(expenseRepository.findById(1L)).thenReturn(Optional.of(testExpense));

        // When
        Optional<ExpenseDTO> result = expenseService.findById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals("Lunch", result.get().getName());
        assertEquals(new BigDecimal("2500.00"), result.get().getPrice());
    }

    @Test
    void testFindById_NotFound_ReturnsEmpty() {
        // Given
        when(expenseRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        Optional<ExpenseDTO> result = expenseService.findById(1L);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void testGetExpenseSummary_WithExpenses() {
        // Given
        when(expenseRepository.sumAllByUser(1L)).thenReturn(new BigDecimal("10000.00"));
        when(expenseRepository.countAllByUser(1L)).thenReturn(4L);
        when(expenseRepository.avgByUser(1L)).thenReturn(new BigDecimal("2500.00"));
        when(expenseRepository.maxByUser(1L)).thenReturn(new BigDecimal("5000.00"));
        when(expenseRepository.minByUser(1L)).thenReturn(new BigDecimal("1000.00"));
        when(expenseRepository.sumByUserAndYearAndMonth(anyLong(), anyInt(), anyInt())).thenReturn(new BigDecimal("3000.00"));
        when(expenseRepository.sumByUserAndYear(anyLong(), anyInt())).thenReturn(new BigDecimal("10000.00"));

        // When
        ExpenseSummaryDTO result = expenseService.getExpenseSummary(1L);

        // Then
        assertNotNull(result);
        assertEquals(new BigDecimal("10000.00"), result.getTotalExpenses());
        assertEquals(4L, result.getExpenseCount());
        assertEquals(new BigDecimal("2500.00"), result.getAverageExpense());
        assertEquals(new BigDecimal("5000.00"), result.getHighestExpense());
        assertEquals(new BigDecimal("1000.00"), result.getLowestExpense());
    }

    @Test
    void testGetExpenseSummary_NoExpenses() {
        // Given
        when(expenseRepository.sumAllByUser(1L)).thenReturn(null);
        when(expenseRepository.countAllByUser(1L)).thenReturn(0L);
        when(expenseRepository.avgByUser(1L)).thenReturn(null);
        when(expenseRepository.maxByUser(1L)).thenReturn(null);
        when(expenseRepository.minByUser(1L)).thenReturn(null);
        when(expenseRepository.sumByUserAndYearAndMonth(anyLong(), anyInt(), anyInt())).thenReturn(null);
        when(expenseRepository.sumByUserAndYear(anyLong(), anyInt())).thenReturn(null);

        // When
        ExpenseSummaryDTO result = expenseService.getExpenseSummary(1L);

        // Then
        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.getTotalExpenses());
        assertEquals(0L, result.getExpenseCount());
        assertEquals(BigDecimal.ZERO, result.getAverageExpense());
        assertEquals(BigDecimal.ZERO, result.getHighestExpense());
        assertEquals(BigDecimal.ZERO, result.getLowestExpense());
    }

    @Test
    void testGetMonthlyStatistics_WithData() {
        // Given
        when(expenseRepository.sumByUserAndYearAndMonth(1L, 2026, 3))
            .thenReturn(new BigDecimal("15000.00"));
        when(expenseRepository.countByUserAndYearAndMonth(1L, 2026, 3))
            .thenReturn(6L);
        @SuppressWarnings("unchecked")
        List<Object[]> categoryBreakdown = (List<Object[]>) (List<?>) Collections.singletonList(
            new Object[]{1L, "Food", "#FF5733", "🍔", new BigDecimal("2500.00"), 1L}
        );
        when(expenseRepository.categorySpendingByDateRange(anyLong(), any(LocalDate.class), any(LocalDate.class)))
            .thenReturn(categoryBreakdown);

        // When
        MonthlyStatisticsDTO result = expenseService.getMonthlyStatistics(1L, 2026, 3);

        // Then
        assertNotNull(result);
        assertEquals(2026, result.getYear());
        assertEquals(3, result.getMonth());
        assertEquals(new BigDecimal("15000.00"), result.getTotalAmount());
        assertEquals(6L, result.getExpenseCount());
        assertEquals(new BigDecimal("2500.00"), result.getAverageExpense());
        assertNotNull(result.getCategoryBreakdown());
    }

    @Test
    void testGetMonthlyStatistics_NoData() {
        // Given
        when(expenseRepository.sumByUserAndYearAndMonth(1L, 2026, 3)).thenReturn(null);
        when(expenseRepository.countByUserAndYearAndMonth(1L, 2026, 3)).thenReturn(0L);
        when(expenseRepository.categorySpendingByDateRange(anyLong(), any(LocalDate.class), any(LocalDate.class)))
            .thenReturn(Arrays.asList());

        // When
        MonthlyStatisticsDTO result = expenseService.getMonthlyStatistics(1L, 2026, 3);

        // Then
        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.getTotalAmount());
        assertEquals(0L, result.getExpenseCount());
        assertEquals(BigDecimal.ZERO, result.getAverageExpense());
    }

    @Test
    void testGetYearlyStatistics_WithData() {
        // Given
        when(expenseRepository.sumByUserAndYear(1L, 2026))
            .thenReturn(new BigDecimal("120000.00"));
        when(expenseRepository.countAllByUser(1L)).thenReturn(48L);
        
        @SuppressWarnings("unchecked")
        List<Object[]> monthlyData = (List<Object[]>) (List<?>) Arrays.asList(
            new Object[]{1, new BigDecimal("10000.00")},
            new Object[]{2, new BigDecimal("12000.00")},
            new Object[]{3, new BigDecimal("15000.00")}
        );
        when(expenseRepository.monthlyBreakdownByYear(eq(1L), eq(2026))).thenReturn(monthlyData);

        // When
        com.spendora.backend.dto.statistics.YearlyStatisticsDTO result = expenseService.getYearlyStatistics(1L, 2026);

        // Then
        assertNotNull(result);
        assertEquals(2026, result.getYear());
        assertEquals(new BigDecimal("120000.00"), result.getTotalAmount());
        assertEquals(48L, result.getExpenseCount());
        assertNotNull(result.getMonthlyBreakdown());
        assertEquals(12, result.getMonthlyBreakdown().size());
    }

    @Test
    void testGetCategorySpending_WithData() {
        // Given
        LocalDate startDate = LocalDate.of(2026, 3, 1);
        LocalDate endDate = LocalDate.of(2026, 3, 31);
        
        @SuppressWarnings("unchecked")
        List<Object[]> categoryData = (List<Object[]>) (List<?>) Arrays.asList(
            new Object[]{1L, "Food", "#FF5733", "🍔", new BigDecimal("5000.00"), 10L},
            new Object[]{2L, "Transport", "#33FF57", "🚗", new BigDecimal("3000.00"), 5L}
        );
        when(expenseRepository.categorySpendingByDateRange(eq(1L), eq(startDate), eq(endDate)))
            .thenReturn(categoryData);

        // When
        List<com.spendora.backend.dto.category.CategorySpendingDTO> result = expenseService.getCategorySpending(1L, startDate, endDate);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Food", result.get(0).getCategoryName());
        assertEquals(new BigDecimal("5000.00"), result.get(0).getTotalAmount());
        assertEquals(10L, result.get(0).getExpenseCount());
        assertTrue(result.get(0).getPercentage() > 0);
    }

    @Test
    void testGetCategorySpending_NoData() {
        // Given
        LocalDate startDate = LocalDate.of(2026, 3, 1);
        LocalDate endDate = LocalDate.of(2026, 3, 31);
        when(expenseRepository.categorySpendingByDateRange(eq(1L), eq(startDate), eq(endDate)))
            .thenReturn(Collections.emptyList());

        // When
        var result = expenseService.getCategorySpending(1L, startDate, endDate);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testListAllExpensesByUser() {
        // Given
        List<Expense> expenses = Arrays.asList(testExpense);
        when(expenseRepository.findByUserId(1L)).thenReturn(expenses);

        // When
        List<ExpenseDTO> result = expenseService.listAllExpensesByUser(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Lunch", result.get(0).getName());
        verify(expenseRepository, times(1)).findByUserId(1L);
    }
}
