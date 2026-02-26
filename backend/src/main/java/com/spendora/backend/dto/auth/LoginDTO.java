package com.spendora.backend.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {
    @NotBlank(message = "A felhasználónév nem lehet üres.")
    private String username;

    @NotBlank(message = "A jelszó nem lehet üres.")
    private String password;

    private String captchaAnswer;
}
