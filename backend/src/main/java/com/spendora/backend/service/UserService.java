package com.spendora.backend.service;

import com.spendora.backend.dto.user.ChangePasswordDTO;
import com.spendora.backend.dto.user.UpdateProfileDTO;
import com.spendora.backend.dto.user.UserDTO;

import java.util.Optional;

public interface UserService {
    Optional<UserDTO> findById(Long id);
    Optional<UserDTO> findByUsername(String username);
    Optional<UserDTO> findByEmail(String email);
    
    // Profile management
    void changePassword(Long userId, ChangePasswordDTO changePasswordDTO);
    UserDTO updateProfile(Long userId, UpdateProfileDTO updateProfileDTO);
    void deleteAccount(Long userId, String password);
}
