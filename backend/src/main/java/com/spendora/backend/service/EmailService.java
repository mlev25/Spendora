package com.spendora.backend.service;

import com.spendora.backend.dto.contact.ContactRequestDTO;

public interface EmailService {
    void sendContactEmail(ContactRequestDTO request);
}
