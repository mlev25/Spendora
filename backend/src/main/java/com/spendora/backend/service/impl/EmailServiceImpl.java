package com.spendora.backend.service.impl;

import com.spendora.backend.dto.contact.ContactRequestDTO;
import com.spendora.backend.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final JavaMailSender mailSender;

    @Value("${contact.recipient.email}")
    private String recipientEmail;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Override
    public void sendContactEmail(ContactRequestDTO request) {
        SimpleMailMessage message = new SimpleMailMessage();

        // Feladó: a te gmail-ed (Google megköveteli)
        message.setFrom(senderEmail);

        // Cél: szintén a te gmail-ed (oda érkezik az üzenet)
        message.setTo(recipientEmail);

        // Reply-To: a beküldő email-je (hogy válaszolhass rá)
        message.setReplyTo(request.getEmail());

        message.setSubject("[Spendora Contact] " + request.getSubject());

        String body = String.format(
            "Új üzenet érkezett a Spendora contact formon!\n\n" +
            "Küldő neve: %s\n" +
            "Küldő email: %s\n" +
            "Tárgy: %s\n\n" +
            "Üzenet:\n%s\n\n" +
            "---\n" +
            "Erre az emailre közvetlenül válaszolva, az üzenet a küldőhöz (%s) kerül.",
            request.getName(),
            request.getEmail(),
            request.getSubject(),
            request.getMessage(),
            request.getEmail()
        );

        message.setText(body);

        mailSender.send(message);
        logger.info("Contact email sent from {} ({})", request.getName(), request.getEmail());
    }
}
