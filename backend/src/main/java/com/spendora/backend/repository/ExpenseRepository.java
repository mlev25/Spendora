package com.spendora.backend.repository;

import com.spendora.backend.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    //Could be useful for querying specified expenses for many diagrams
    //TODO expand this list if more specific methods are required in the future

    List<Expense> findByUserId(Long userId);
    List<Expense> findByCategoryId(Long categoryId);
    List<Expense> findByDateBetween(LocalDate start, LocalDate end);
    List<Expense> findByUserIdAndDateBetween(Long userId, LocalDate start, LocalDate end);

    //Top tiers if needed
    List<Expense> findTop10ByUserIdOrderByPriceDesc(Long userId);

    @Query("""
        SELECT e.category.name, SUM(e.price) as total
        FROM Expense e
        WHERE e.user.id = :userId
        GROUP BY e.category.name
        ORDER BY total DESC
    """)
    List<Object[]> findTopCategoriesByUser(@Param("userId") Long userId);

    @Query("SELECT SUM(e.price) FROM Expense e WHERE e.user.id = :userId AND e.date BETWEEN :start AND :end")
    BigDecimal sumExpensesByUserAndDate(@Param("userId") Long userId,
                                        @Param("start") LocalDate start,
                                        @Param("end") LocalDate end);

    @Query("SELECT e.category.name, SUM(e.price) FROM Expense e WHERE e.user.id = :userId GROUP BY e.category.name")
    List<Object[]> sumExpensesByCategory(@Param("userId") Long userId);
}
