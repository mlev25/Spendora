package com.spendora.backend.controller;

import com.spendora.backend.dto.ExpenseDTO;
import com.spendora.backend.service.impl.ExpenseServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/expenses")
@RequiredArgsConstructor
public class ExpenseController {
    private final ExpenseServiceImpl expenseService;

    //TODO needs way more functionalities
    @PostMapping
    public ResponseEntity<?> addNewExpense(@RequestBody ExpenseDTO expenseDTO){
        try {
            ExpenseDTO savedExpense = expenseService.addExpense(expenseDTO);
            return ResponseEntity.ok(savedExpense);

        }catch (IllegalArgumentException e){
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }
}
