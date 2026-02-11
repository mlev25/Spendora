package com.spendora.backend.controller;

import com.spendora.backend.config.jwt.UserPrincipal;
import com.spendora.backend.dto.ExpenseDTO;
import com.spendora.backend.dto.PredictCategoryRequest;
import com.spendora.backend.service.GeminiService;
import com.spendora.backend.service.impl.ExpenseServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {
    private final ExpenseServiceImpl expenseService;
    private final GeminiService geminiService;

    @PostMapping
    public ResponseEntity<?> addNewExpense(@Valid @RequestBody ExpenseDTO expenseDTO, Authentication authentication){
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }
        
        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            expenseDTO.setUserId(userPrincipal.getId()); // Force current user ID
            
            ExpenseDTO savedExpense = expenseService.addExpense(expenseDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedExpense);
        } catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getMyExpenses(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }
        
        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            System.out.println(userPrincipal.getId() + userPrincipal.getName());
            List<ExpenseDTO> expenses = expenseService.getAllExpenses(userPrincipal.getId());
            return ResponseEntity.ok(expenses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getExpenseById(@PathVariable Long id, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }
        
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Optional<ExpenseDTO> expense = expenseService.findById(id);
        
        // Check if expense belongs to current user
        if (expense.isPresent() && !expense.get().getUserId().equals(userPrincipal.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can only access your own expenses");
        }
        
        return expense.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateExpense(@PathVariable Long id, @Valid @RequestBody ExpenseDTO expenseDTO, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }
        
        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            
            // Check ownership before update
            Optional<ExpenseDTO> existing = expenseService.findById(id);
            if (existing.isPresent() && !existing.get().getUserId().equals(userPrincipal.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can only update your own expenses");
            }
            
            expenseDTO.setUserId(userPrincipal.getId()); // Force current user ID
            Optional<ExpenseDTO> updated = expenseService.updateExpense(id, expenseDTO);
            return updated.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExpense(@PathVariable Long id, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }
        
        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            
            // Check ownership before delete
            Optional<ExpenseDTO> existing = expenseService.findById(id);
            if (existing.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            if (!existing.get().getUserId().equals(userPrincipal.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can only delete your own expenses");
            }
            
            expenseService.deleteExpense(id);
            return ResponseEntity.ok("Expense deleted successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/predict-category")
    public ResponseEntity<?> predictCategory(@Valid @RequestBody PredictCategoryRequest request, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }
        
        try {
            String predictedCategory = geminiService.predictCategory(request);
            
            Map<String, String> response = new HashMap<>();
            response.put("categoryName", predictedCategory);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error predicting category: " + e.getMessage());
        }
    }
}
