package com.spendora.backend.service.impl;

import com.spendora.backend.dto.RegisterDTO;
import com.spendora.backend.dto.UserDTO;
import com.spendora.backend.entity.User;
import com.spendora.backend.repository.UserRepository;
import com.spendora.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    //Registration
    public UserDTO register(RegisterDTO registerDTO){
        if (userRepository.existsByEmail(registerDTO.getEmail())) {
            throw new IllegalArgumentException("Email már létezik!");
        }
        if (userRepository.existsByUsername(registerDTO.getUsername())) {
            throw new IllegalArgumentException("Username már létezik!");
        }

        User user = new User();
        user.setFirstName(registerDTO.getFirstName());
        user.setLastName(registerDTO.getLastName());
        user.setUsername(registerDTO.getUsername());
        user.setEmail(registerDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));

        User savedUser = userRepository.save(user);
        return toDTO(savedUser);
    }

    //Simplified Login, needs modification
    //TODO JWT integration
    public Optional<UserDTO> login(String username, String rawPassword) throws IllegalArgumentException{
        User identifiedUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Hibas username vagy jelszo megadva!"));
        if(!passwordEncoder.matches(rawPassword, identifiedUser.getPassword())){
            throw new IllegalArgumentException("Hibas email vagy jelszo!");
        }
        return Optional.of(toDTO(identifiedUser));
    }

    @Override
    public Optional<UserDTO> findById(Long id) {
        Optional<User> foundUser = userRepository.findById(id);
        return foundUser.map(this::toDTO);
    }

    @Override
    public Optional<UserDTO> findByEmail(String email) {
        return userRepository.findByEmail(email).map(this::toDTO);
    }

    //Converting to DTO
    private UserDTO toDTO(User user){
        return new UserDTO(user.getId(), user.getUsername(), user.getEmail());
    }
}
