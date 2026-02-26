package com.spendora.backend.controller;

import com.spendora.backend.dto.contact.ContactRequestDTO;
import com.spendora.backend.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/contact")
@RequiredArgsConstructor
public class ContactController {

    private final EmailService emailService;

    @PostMapping
    public ResponseEntity<?> sendContactMessage(@Valid @RequestBody ContactRequestDTO request) {
        try {
            emailService.sendContactEmail(request);
            return ResponseEntity.ok(Map.of("message", "Üzenet sikeresen elküldve!"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Az üzenet küldése sikertelen. Kérjük, próbálja újra később."));
        }
    }
}
