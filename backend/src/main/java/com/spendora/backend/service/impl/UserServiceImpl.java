package com.spendora.backend.service.impl;

import com.spendora.backend.dto.ChangePasswordDTO;
import com.spendora.backend.dto.UpdateProfileDTO;
import com.spendora.backend.dto.UserDTO;
import com.spendora.backend.entity.User;
import com.spendora.backend.repository.UserRepository;
import com.spendora.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Optional<UserDTO> findById(Long id) {
        return userRepository.findById(id).map(this::toDTO);
    }

    @Override
    public Optional<UserDTO> findByUsername(String username) {
        User user = userRepository.findByUsername(username);
        return user != null ? Optional.of(toDTO(user)) : Optional.empty();
    }

    @Override
    public Optional<UserDTO> findByEmail(String email) {
        return userRepository.findByEmail(email).map(this::toDTO);
    }

    @Override
    @Transactional
    public void changePassword(Long userId, ChangePasswordDTO changePasswordDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Verify current password
        if (!passwordEncoder.matches(changePasswordDTO.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        // Set new password
        user.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public UserDTO updateProfile(Long userId, UpdateProfileDTO updateProfileDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Check if username is already taken by another user
        User existingUser = userRepository.findByUsername(updateProfileDTO.getUsername());
        if (existingUser != null && !existingUser.getId().equals(userId)) {
            throw new IllegalArgumentException("Username is already taken");
        }

        user.setName(updateProfileDTO.getName());
        user.setUsername(updateProfileDTO.getUsername());
        
        User savedUser = userRepository.save(user);
        return toDTO(savedUser);
    }

    @Override
    @Transactional
    public void deleteAccount(Long userId, String password) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Verify password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Password is incorrect");
        }

        // Delete user (cascade will delete all expenses)
        userRepository.delete(user);
    }

    private UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        
        if (user.getRoles() != null) {
            List<String> roleNames = user.getRoles().stream()
                    .map(role -> role.getName().name())
                    .collect(Collectors.toList());
            dto.setRoles(roleNames);
        }
        
        dto.setLastLoginDate(user.getLastLoginDate());
        return dto;
    }
}
