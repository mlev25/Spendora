package com.spendora.backend.service;

import com.spendora.backend.dto.ExpenseDTO;

import java.util.List;
import java.util.Optional;

public interface ExpenseService {
    ExpenseDTO addExpense(ExpenseDTO expenseDTO);
    List<ExpenseDTO> getAllExpenses(Long userId);
    Optional<ExpenseDTO> updateExpense(Long id, ExpenseDTO expenseDTO);
    void deleteExpense(Long id);
    Optional<ExpenseDTO> findById(Long id);
    List<ExpenseDTO> listAllExpensesByUser(Long userId);
}
