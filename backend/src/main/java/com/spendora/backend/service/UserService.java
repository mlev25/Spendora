package com.spendora.backend.service;

import com.spendora.backend.dto.UserDTO;

import java.util.Optional;

public interface UserService {
    Optional<UserDTO> findById(Long id);
    Optional<UserDTO> findByUsername(String username);
    Optional<UserDTO> findByEmail(String email);
}
