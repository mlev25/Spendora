package com.spendora.backend.service;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.spendora.backend.config.jwt.JwtUtils;
import com.spendora.backend.config.jwt.UserPrincipal;
import com.spendora.backend.dto.auth.LoginDTO;
import com.spendora.backend.dto.auth.RegisterDTO;
import com.spendora.backend.entity.Role;
import com.spendora.backend.entity.User;
import com.spendora.backend.entity.util.RoleName;
import com.spendora.backend.exception.CaptchaRequiredException;
import com.spendora.backend.repository.RoleRepository;
import com.spendora.backend.repository.UserRepository;
import com.spendora.backend.service.impl.AuthServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthServiceImpl
 * Tests authentication, registration, and CAPTCHA verification logic
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private DefaultKaptcha captchaProducer;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpSession session;

    @Mock
    private Authentication authentication;

    @Mock
    private UserPrincipal userPrincipal;

    @InjectMocks
    private AuthServiceImpl authService;

    private User testUser;
    private LoginDTO loginDTO;
    private Role userRole;

    @BeforeEach
    void setUp() {
        // Setup test role
        userRole = new Role();
        userRole.setId(1L);
        userRole.setName(RoleName.USER);

        // Setup test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setFailedLoginAttempts(0);
        
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        testUser.setRoles(roles);

        // Setup login DTO
        loginDTO = new LoginDTO();
        loginDTO.setUsername("testuser");
        loginDTO.setPassword("password123");
    }

    @Test
    void testAuthenticateUser_Success_NoFailedAttempts() {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(testUser);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(jwtUtils.generateJwtToken(userPrincipal)).thenReturn("jwt-token-123");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        String token = authService.authenticateUser(loginDTO, request);

        // Then
        assertNotNull(token);
        assertEquals("jwt-token-123", token);
        verify(userRepository, times(1)).save(any(User.class)); // Once for success handler
        verify(session, never()).getAttribute("captchaCode"); // No CAPTCHA needed
    }

    @Test
    void testAuthenticateUser_FailedAttempts_NoCaptcha() {
        // Given
        testUser.setFailedLoginAttempts(2);
        when(userRepository.findByUsername("testuser")).thenReturn(testUser);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenThrow(new BadCredentialsException("Bad credentials"));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When & Then
        assertThrows(BadCredentialsException.class, 
            () -> authService.authenticateUser(loginDTO, request));
        
        verify(userRepository, times(1)).save(any(User.class)); // Increment failed attempts
    }

    @Test
    void testAuthenticateUser_RequiresCaptcha_MissingCaptcha() {
        // Given
        testUser.setFailedLoginAttempts(3);
        loginDTO.setCaptchaAnswer(null);
        
        when(request.getSession()).thenReturn(session);
        when(userRepository.findByUsername("testuser")).thenReturn(testUser);
        when(session.getAttribute("captchaCode")).thenReturn(null);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When & Then
        CaptchaRequiredException exception = assertThrows(
            CaptchaRequiredException.class,
            () -> authService.authenticateUser(loginDTO, request)
        );
        assertEquals("CAPTCHA ellenorzes sikertelen.", exception.getMessage());
        verify(userRepository, times(1)).save(any(User.class)); // Increment failed attempts
    }

    @Test
    void testAuthenticateUser_RequiresCaptcha_WrongCaptcha() {
        // Given
        testUser.setFailedLoginAttempts(3);
        loginDTO.setCaptchaAnswer("WRONG");
        
        when(request.getSession()).thenReturn(session);
        when(userRepository.findByUsername("testuser")).thenReturn(testUser);
        when(session.getAttribute("captchaCode")).thenReturn("ABCD");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When & Then
        CaptchaRequiredException exception = assertThrows(
            CaptchaRequiredException.class,
            () -> authService.authenticateUser(loginDTO, request)
        );
        assertEquals("CAPTCHA ellenorzes sikertelen.", exception.getMessage());
    }

    @Test
    void testAuthenticateUser_RequiresCaptcha_CorrectCaptcha_Success() {
        // Given
        testUser.setFailedLoginAttempts(3);
        loginDTO.setCaptchaAnswer("ABCD");
        
        when(request.getSession()).thenReturn(session);
        when(userRepository.findByUsername("testuser")).thenReturn(testUser);
        when(session.getAttribute("captchaCode")).thenReturn("ABCD");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(jwtUtils.generateJwtToken(userPrincipal)).thenReturn("jwt-token-123");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        String token = authService.authenticateUser(loginDTO, request);

        // Then
        assertNotNull(token);
        assertEquals("jwt-token-123", token);
        verify(session, times(1)).removeAttribute("captchaCode");
    }

    @Test
    void testAuthenticateUser_CaptchaIsCaseInsensitive() {
        // Given
        testUser.setFailedLoginAttempts(3);
        loginDTO.setCaptchaAnswer("abcd"); // lowercase
        
        when(request.getSession()).thenReturn(session);
        when(userRepository.findByUsername("testuser")).thenReturn(testUser);
        when(session.getAttribute("captchaCode")).thenReturn("ABCD"); // uppercase
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(jwtUtils.generateJwtToken(userPrincipal)).thenReturn("jwt-token-123");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        String token = authService.authenticateUser(loginDTO, request);

        // Then
        assertNotNull(token);
        assertEquals("jwt-token-123", token);
    }

    @Test
    void testAuthenticateUser_ThirdFailure_ThrowsCaptchaRequired() {
        // Given
        testUser.setFailedLoginAttempts(2); // Will become 3 after this failure
        
        when(userRepository.findByUsername("testuser")).thenReturn(testUser);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenThrow(new BadCredentialsException("Bad credentials"));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setFailedLoginAttempts(3);
            return u;
        });

        // When & Then
        CaptchaRequiredException exception = assertThrows(
            CaptchaRequiredException.class,
            () -> authService.authenticateUser(loginDTO, request)
        );
        assertEquals("Túl sok sikertelen próbálkozás, CAPTCHA szukseges.", exception.getMessage());
    }

    @Test
    void testRegisterUser_Success() {
        // Given
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setName("New User");
        registerDTO.setUsername("newuser");
        registerDTO.setEmail("new@example.com");
        registerDTO.setPassword("password123");
        registerDTO.setRole("USER");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(encoder.encode("password123")).thenReturn("encodedPassword");
        when(roleRepository.findByName(RoleName.USER)).thenReturn(userRole);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        User result = authService.registerUser(registerDTO);

        // Then
        assertNotNull(result);
        verify(userRepository, times(1)).save(any(User.class));
        verify(encoder, times(1)).encode("password123");
    }

    @Test
    void testRegisterUser_UsernameAlreadyExists_ThrowsException() {
        // Given
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setUsername("existinguser");
        registerDTO.setEmail("new@example.com");

        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        // When & Then
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> authService.registerUser(registerDTO)
        );
        assertTrue(exception.getMessage().contains("felhasználónév már foglalt"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testRegisterUser_EmailAlreadyExists_ThrowsException() {
        // Given
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setUsername("newuser");
        registerDTO.setEmail("existing@example.com");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        // When & Then
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> authService.registerUser(registerDTO)
        );
        assertTrue(exception.getMessage().contains("email cím már használatban"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testRegisterUser_InvalidRole_ThrowsException() {
        // Given
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setName("New User");
        registerDTO.setUsername("newuser");
        registerDTO.setEmail("new@example.com");
        registerDTO.setPassword("password123");
        registerDTO.setRole("INVALID_ROLE");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(encoder.encode("password123")).thenReturn("encodedPassword");

        // When & Then
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> authService.registerUser(registerDTO)
        );
        assertTrue(exception.getMessage().contains("role does not exist"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testHandleLoginSuccess_ResetsFailedAttempts() {
        // Given
        testUser.setFailedLoginAttempts(5);
        loginDTO.setCaptchaAnswer("ABCD");
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("captchaCode")).thenReturn("ABCD");
        when(userRepository.findByUsername("testuser")).thenReturn(testUser);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(jwtUtils.generateJwtToken(userPrincipal)).thenReturn("jwt-token-123");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            // Verify failed attempts reset to 0
            assertEquals(0, u.getFailedLoginAttempts());
            return u;
        });

        // When
        authService.authenticateUser(loginDTO, request);

        // Then
        verify(userRepository, times(1)).save(any(User.class));
    }
}
