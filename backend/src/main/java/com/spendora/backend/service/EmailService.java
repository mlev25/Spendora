package com.spendora.backend.service;

import com.spendora.backend.dto.ContactRequestDTO;

public interface EmailService {
    void sendContactEmail(ContactRequestDTO request);
}
