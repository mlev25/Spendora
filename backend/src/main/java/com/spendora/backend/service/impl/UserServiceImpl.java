package com.spendora.backend.service.impl;

import com.spendora.backend.dto.UserDTO;
import com.spendora.backend.entity.User;
import com.spendora.backend.repository.UserRepository;
import com.spendora.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

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

    private UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setName(user.getName());
        
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
