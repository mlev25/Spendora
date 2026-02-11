package com.spendora.backend.controller;

import com.spendora.backend.config.jwt.UserPrincipal;
import com.spendora.backend.dto.*;
import com.spendora.backend.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {
    
    private final ExpenseService expenseService;

    /**
     * GET /api/statistics/summary
     * Quick KPIs: total, averages, highs, lows
     */
    @GetMapping("/summary")
    public ResponseEntity<?> getExpenseSummary(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }

        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            ExpenseSummaryDTO summary = expenseService.getExpenseSummary(userPrincipal.getId());
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching expense summary: " + e.getMessage());
        }
    }

    /**
     * GET /api/statistics/monthly?year=2026&month=2
     * Monthly statistics with category breakdown
     */
    @GetMapping("/monthly")
    public ResponseEntity<?> getMonthlyStatistics(
            @RequestParam Integer year,
            @RequestParam Integer month,
            Authentication authentication) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }

        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            MonthlyStatisticsDTO stats = expenseService.getMonthlyStatistics(
                    userPrincipal.getId(), year, month);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching monthly statistics: " + e.getMessage());
        }
    }

    /**
     * GET /api/statistics/yearly?year=2026
     * Yearly statistics with 12-month breakdown
     */
    @GetMapping("/yearly")
    public ResponseEntity<?> getYearlyStatistics(
            @RequestParam Integer year,
            Authentication authentication) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }

        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            YearlyStatisticsDTO stats = expenseService.getYearlyStatistics(
                    userPrincipal.getId(), year);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching yearly statistics: " + e.getMessage());
        }
    }

    /**
     * GET /api/statistics/categories?startDate=2026-01-01&endDate=2026-12-31
     * Category spending breakdown with percentages
     */
    @GetMapping("/categories")
    public ResponseEntity<?> getCategorySpending(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Authentication authentication) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }

        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            List<CategorySpendingDTO> categoryStats = expenseService.getCategorySpending(
                    userPrincipal.getId(), startDate, endDate);
            return ResponseEntity.ok(categoryStats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching category spending: " + e.getMessage());
        }
    }
}
