package com.spendora.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    @NotNull(message = "ID must not be null!")
    private Long id;

    @NotBlank(message = "The Given username must not be blank")
    private String username;

    @NotBlank(message = "The given name must not be blank")
    private String name;

    private List<String> roles;
    private Date lastLoginDate;
}
