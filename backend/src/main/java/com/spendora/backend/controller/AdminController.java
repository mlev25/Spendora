package com.spendora.backend.controller;

import com.spendora.backend.dto.UserDTO;
import com.spendora.backend.entity.User;
import com.spendora.backend.repository.ExpenseRepository;
import com.spendora.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;

    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserDTO> userDTOs = users.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDTOs);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getGlobalStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // Összes felhasználó száma
        long totalUsers = userRepository.count();
        
        // Összes kiadás száma
        long totalExpenses = expenseRepository.count();
        
        // Összes kiadás összege
        double totalAmount = expenseRepository.findAll().stream()
                .mapToDouble(e -> e.getPrice().doubleValue())
                .sum();
        
        // Átlagos kiadás per user
        double avgExpensePerUser = totalUsers > 0 ? totalAmount / totalUsers : 0;
        
        // Legnagyobb egyszeri kiadás
        double maxExpense = expenseRepository.findAll().stream()
                .mapToDouble(e -> e.getPrice().doubleValue())
                .max()
                .orElse(0);
        
        stats.put("totalUsers", totalUsers);
        stats.put("totalExpenses", totalExpenses);
        stats.put("totalAmount", totalAmount);
        stats.put("avgExpensePerUser", avgExpensePerUser);
        stats.put("maxExpense", maxExpense);
        
        return ResponseEntity.ok(stats);
    }

    private UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        
        if (user.getRoles() != null) {
            List<String> roleNames = user.getRoles().stream()
                    .map(role -> role.getName().name())
                    .collect(Collectors.toList());
            dto.setRoles(roleNames);
        }
        
        dto.setLastLoginDate(user.getLastLoginDate());
        return dto;
    }
}
