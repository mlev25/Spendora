package com.spendora.backend.service;

import com.spendora.backend.dto.RegisterDTO;
import com.spendora.backend.dto.UserDTO;

import java.util.Optional;

public interface UserService {
    UserDTO register(RegisterDTO registerDTO);
    Optional<UserDTO> login(String email, String rawPassword);
    Optional<UserDTO> findById(Long id);
    Optional<UserDTO> findByEmail(String email);
}
