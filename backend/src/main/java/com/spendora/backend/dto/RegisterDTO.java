package com.spendora.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDTO {
    @NotBlank(message = "Username must not be blank!")
    private String username;

    @Email(message = "The given email should be valid")
    @NotBlank(message = "The given email must not be blank")
    private String email;

    @NotBlank(message = "The Given password must not be blank")
    @Size(min = 6, message = "Given password must be at least 6 characters")
    private String password;
}
