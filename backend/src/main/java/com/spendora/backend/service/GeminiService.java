package com.spendora.backend.service;

import com.spendora.backend.dto.expense.PredictCategoryRequest;

public interface GeminiService {
    /**
     * Predicts the most suitable category for an expense using Gemini AI
     * @param request Expense details (name, price, description)
     * @return Category name suggested by AI
     */
    String predictCategory(PredictCategoryRequest request);
}
