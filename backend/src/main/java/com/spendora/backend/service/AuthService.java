package com.spendora.backend.service;

import com.spendora.backend.dto.LoginDTO;
import com.spendora.backend.dto.RegisterDTO;
import com.spendora.backend.dto.UserDTO;
import com.spendora.backend.entity.User;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {

    String authenticateUser(LoginDTO loginRequest, HttpServletRequest request);

    User registerUser(RegisterDTO signUpRequest);

    UserDTO mapToUserDto(User user);
}