package com.spendora.backend.controller;

import com.spendora.backend.config.jwt.UserPrincipal;
import com.spendora.backend.dto.ChatRequestDTO;
import com.spendora.backend.dto.ChatResponseDTO;
import com.spendora.backend.service.impl.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/ask")
    public ResponseEntity<ChatResponseDTO> askChatbot(
            @RequestBody ChatRequestDTO request,
            Authentication authentication) {
        
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long userId = userPrincipal.getId();
        
        String response = chatService.getChatResponse(userId, request.getMessages());
        
        return ResponseEntity.ok(new ChatResponseDTO(response));
    }
}
