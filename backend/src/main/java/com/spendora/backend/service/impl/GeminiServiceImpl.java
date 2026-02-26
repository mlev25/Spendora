package com.spendora.backend.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spendora.backend.dto.expense.PredictCategoryRequest;
import com.spendora.backend.entity.Category;
import com.spendora.backend.repository.CategoryRepository;
import com.spendora.backend.service.GeminiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GeminiServiceImpl implements GeminiService {

    private static final Logger logger = LoggerFactory.getLogger(GeminiServiceImpl.class);

    @Value("${gemini.api.key:}")
    private String geminiApiKey;

    @Value("${gemini.api.url:https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent}")
    private String geminiApiUrl;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public String predictCategory(PredictCategoryRequest request) {
        // Ha nincs API key, default kategóriát adunk vissza
        if (geminiApiKey == null || geminiApiKey.isEmpty()) {
            logger.warn("Gemini API key not configured. Returning default category.");
            return "Egyéb";
        }

        try {
            // Kategóriák lekérése
            List<Category> categories = categoryRepository.findAll();
            String categoryNames = categories.stream()
                    .map(Category::getName)
                    .collect(Collectors.joining(", "));

            // Prompt összeállítása
            String prompt = buildPrompt(request, categoryNames);

            // API hívás
            String response = callGeminiApi(prompt);

            // Válasz tisztítása és validálása
            String predictedCategory = cleanAndValidateCategory(response, categories);

            logger.info("Predicted category: {} for expense: {}", predictedCategory, request.getName());
            return predictedCategory;

        } catch (Exception e) {
            logger.error("Error predicting category with Gemini API: {}", e.getMessage());
            return "Egyéb"; // Default fallback
        }
    }

    private String buildPrompt(PredictCategoryRequest request, String categoryNames) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Egy kiadás kategorizálási feladat következik.\n\n");
        prompt.append("Kiadás részletei:\n");
        prompt.append("- Név: ").append(request.getName()).append("\n");
        
        if (request.getPrice() != null) {
            prompt.append("- Összeg: ").append(request.getPrice()).append(" Ft\n");
        }
        
        if (request.getDescription() != null && !request.getDescription().isEmpty()) {
            prompt.append("- Leírás: ").append(request.getDescription()).append("\n");
        }
        
        prompt.append("\nElérhető kategóriák: ").append(categoryNames).append("\n\n");
        prompt.append("Válassz egyetlen legmegfelelőbb kategóriát a fenti listából!\n");
        prompt.append("FONTOS: A válaszod CSAK a kategória neve legyen, semmi más szöveg vagy magyarázat!\n");
        prompt.append("Kategória:");

        return prompt.toString();
    }

    private String callGeminiApi(String prompt) throws Exception {
        // API URL API key-vel
        String urlWithKey = geminiApiUrl + "?key=" + geminiApiKey;

        // Request body építése
        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> content = new HashMap<>();
        Map<String, String> part = new HashMap<>();
        
        part.put("text", prompt);
        content.put("parts", List.of(part));
        requestBody.put("contents", List.of(content));

        // Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        // HTTP POST request
        ResponseEntity<String> response = restTemplate.exchange(
                urlWithKey,
                HttpMethod.POST,
                entity,
                String.class
        );

        // Parse JSON response
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response.getBody());
        
        String text = root.path("candidates")
                .get(0)
                .path("content")
                .path("parts")
                .get(0)
                .path("text")
                .asText();

        return text.trim();
    }

    private String cleanAndValidateCategory(String response, List<Category> categories) {
        // Tisztítás: whitespace, extra sorok, írásejelek eltávolítása
        String cleaned = response.trim()
                .replaceAll("\\n", "")
                .replaceAll("[.,:;!?]", "")
                .trim();

        // Ellenőrzés: létezik-e a kategória
        for (Category category : categories) {
            if (category.getName().equalsIgnoreCase(cleaned)) {
                return category.getName();
            }
        }

        // Ha nem találjuk meg pontosan, fuzzy match (tartalmazza-e)
        for (Category category : categories) {
            if (cleaned.toLowerCase().contains(category.getName().toLowerCase()) ||
                category.getName().toLowerCase().contains(cleaned.toLowerCase())) {
                return category.getName();
            }
        }

        // Default fallback
        logger.warn("Could not match AI response '{}' to any category", cleaned);
        return "Egyéb";
    }
}
