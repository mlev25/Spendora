package com.spendora.backend.service.impl;

import com.spendora.backend.dto.ExpenseDTO;
import com.spendora.backend.entity.Expense;
import com.spendora.backend.repository.CategoryRepository;
import com.spendora.backend.repository.ExpenseRepository;
import com.spendora.backend.repository.UserRepository;
import com.spendora.backend.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public ExpenseDTO addExpense(ExpenseDTO expenseDTO) {
        return null;
    }

    @Override
    public List<ExpenseDTO> getAllExpenses(Long userId) {
        return List.of();
    }

    @Override
    public Optional<ExpenseDTO> updateExpense(Long id, ExpenseDTO expenseDTO) {
        return Optional.empty();
    }

    @Override
    public void deleteExpense(Long id) {
        Optional<Expense> foundExpense = expenseRepository.findById(id);

    }

    @Override
    public Optional<ExpenseDTO> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<ExpenseDTO> listAllExpensesByUser(Long userId) {
        return List.of();
    }
}
