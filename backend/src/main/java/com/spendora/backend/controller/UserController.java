package com.spendora.backend.controller;

import com.spendora.backend.config.jwt.UserPrincipal;
import com.spendora.backend.dto.UserDTO;
import com.spendora.backend.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserServiceImpl userService;

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Not authenticated");
        }
        
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Optional<UserDTO> userDTO = userService.findById(userPrincipal.getId());
        
        return userDTO.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    // REMOVED: Security risk - users should only access their own data via /me endpoint
    // If you need user lookup by ID, add @PreAuthorize("hasRole('ADMIN')") annotation
}
