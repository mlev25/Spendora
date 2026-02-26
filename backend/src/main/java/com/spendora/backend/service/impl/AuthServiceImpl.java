package com.spendora.backend.service.impl;

import com.spendora.backend.config.jwt.JwtUtils;
import com.spendora.backend.config.jwt.UserPrincipal;
import com.spendora.backend.dto.auth.LoginDTO;
import com.spendora.backend.dto.auth.RegisterDTO;
import com.spendora.backend.dto.user.UserDTO;
import com.spendora.backend.entity.Role;
import com.spendora.backend.entity.User;
import com.spendora.backend.entity.util.RoleName;
import com.spendora.backend.exception.CaptchaRequiredException;
import com.spendora.backend.repository.RoleRepository;
import com.spendora.backend.repository.UserRepository;
import com.spendora.backend.service.AuthService;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;

    private final DefaultKaptcha captchaProducer;

    public AuthServiceImpl(AuthenticationManager authenticationManager, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder encoder, JwtUtils jwtUtils, DefaultKaptcha captchaProducer) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
        this.captchaProducer = captchaProducer;
    }

    @Override
    public String authenticateUser(LoginDTO loginRequest, HttpServletRequest request) {
        User user = userRepository.findByUsername(loginRequest.getUsername());
        final int CAPTCHA_LIMIT = 3;

        if (user != null && user.getFailedLoginAttempts() >= CAPTCHA_LIMIT) {
            String expectedCaptcha = (String) request.getSession().getAttribute("captchaCode");
            if (expectedCaptcha == null || !expectedCaptcha.equalsIgnoreCase(loginRequest.getCaptchaAnswer())) {
                handleLoginFailure(user);
                throw new CaptchaRequiredException("CAPTCHA ellenorzes sikertelen.");
            }
            request.getSession().removeAttribute("captchaCode");
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
            User authenticatedUser = userRepository.findByUsername(loginRequest.getUsername());
            if (authenticatedUser != null) {
                handleLoginSuccess(authenticatedUser);
            }
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return jwtUtils.generateJwtToken((UserPrincipal) authentication.getPrincipal());
        } catch (AuthenticationException e) {

            User failedUser = userRepository.findByUsername(loginRequest.getUsername());

            if (failedUser != null) {
                handleLoginFailure(failedUser);

                if (failedUser.getFailedLoginAttempts() >= CAPTCHA_LIMIT) {
                    throw new CaptchaRequiredException("Túl sok sikertelen próbálkozás, CAPTCHA szukseges.");
                }
            }

            throw e;
        }
    }

    @Override
    public User registerUser(RegisterDTO signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new RuntimeException("Error: A felhasználónév már foglalt!");
        }
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new RuntimeException("Error: Az email cím már használatban van!");
        }

        User user = new User();
        user.setName(signUpRequest.getName());
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(encoder.encode(signUpRequest.getPassword()));

        Set<Role> roles = new HashSet<>();
        String strRole = signUpRequest.getRole().toUpperCase();

        RoleName roleName;
        try {
            roleName = RoleName.valueOf(strRole);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Error: The role does not exist: " + strRole);
        }

        Role userRole = roleRepository.findByName(roleName);

        roles.add(userRole);
        user.setRoles(roles);

        return userRepository.save(user);
    }

    @Override
    public UserDTO mapToUserDto(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setName(user.getName());

        List<String> roleNames = user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toList());

        dto.setRoles(roleNames);
        dto.setLastLoginDate(user.getLastLoginDate());
        return dto;
    }

    private void handleLoginSuccess(User user) {
        user.setFailedLoginAttempts(0);
        user.setLastLoginDate(new Date());
        userRepository.save(user);
    }

    private void handleLoginFailure(User user) {
        user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
        userRepository.save(user);
    }
}
