package com.spendora.backend.service.impl;

import com.spendora.backend.dto.ExpenseDTO;
import com.spendora.backend.entity.Category;
import com.spendora.backend.entity.Expense;
import com.spendora.backend.entity.User;
import com.spendora.backend.repository.CategoryRepository;
import com.spendora.backend.repository.ExpenseRepository;
import com.spendora.backend.repository.UserRepository;
import com.spendora.backend.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
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

}
