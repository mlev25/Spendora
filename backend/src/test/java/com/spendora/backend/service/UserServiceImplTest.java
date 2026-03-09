package com.spendora.backend.service;

import com.spendora.backend.dto.user.ChangePasswordDTO;
import com.spendora.backend.dto.user.UpdateProfileDTO;
import com.spendora.backend.dto.user.UserDTO;
import com.spendora.backend.entity.Role;
import com.spendora.backend.entity.User;
import com.spendora.backend.entity.util.RoleName;
import com.spendora.backend.repository.UserRepository;
import com.spendora.backend.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserServiceImpl
 * Tests user management operations including profile updates and password changes
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private Role userRole;

    @BeforeEach
    void setUp() {
        userRole = new Role();
        userRole.setId(Long.valueOf(1));
        userRole.setName(RoleName.USER);

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword123");
        testUser.setRoles(roles);
    }

    @Test
    void testFindById_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        Optional<UserDTO> result = userService.findById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
        assertEquals("Test User", result.get().getName());
        assertEquals("test@example.com", result.get().getEmail());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testFindById_NotFound_ReturnsEmpty() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        Optional<UserDTO> result = userService.findById(1L);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void testFindByUsername_Success() {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(testUser);

        // When
        Optional<UserDTO> result = userService.findByUsername("testuser");

        // Then
        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void testFindByUsername_NotFound_ReturnsEmpty() {
        // Given
        when(userRepository.findByUsername("nonexistent")).thenReturn(null);

        // When
        Optional<UserDTO> result = userService.findByUsername("nonexistent");

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void testFindByEmail_Success() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // When
        Optional<UserDTO> result = userService.findByEmail("test@example.com");

        // Then
        assertTrue(result.isPresent());
        assertEquals("test@example.com", result.get().getEmail());
    }

    @Test
    void testChangePassword_Success() {
        // Given
        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO();
        changePasswordDTO.setCurrentPassword("oldPassword");
        changePasswordDTO.setNewPassword("newPassword123");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("oldPassword", "encodedPassword123")).thenReturn(true);
        when(passwordEncoder.encode("newPassword123")).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        userService.changePassword(1L, changePasswordDTO);

        // Then
        verify(passwordEncoder, times(1)).matches("oldPassword", "encodedPassword123");
        verify(passwordEncoder, times(1)).encode("newPassword123");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testChangePassword_UserNotFound_ThrowsException() {
        // Given
        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO();
        changePasswordDTO.setCurrentPassword("oldPassword");
        changePasswordDTO.setNewPassword("newPassword123");

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.changePassword(1L, changePasswordDTO)
        );
        assertEquals("User not found", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void testChangePassword_IncorrectCurrentPassword_ThrowsException() {
        // Given
        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO();
        changePasswordDTO.setCurrentPassword("wrongPassword");
        changePasswordDTO.setNewPassword("newPassword123");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword123")).thenReturn(false);

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.changePassword(1L, changePasswordDTO)
        );
        assertEquals("Current password is incorrect", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void testUpdateProfile_Success() {
        // Given
        UpdateProfileDTO updateProfileDTO = new UpdateProfileDTO();
        updateProfileDTO.setName("Updated Name");
        updateProfileDTO.setUsername("updateduser");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findByUsername("updateduser")).thenReturn(null);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        UserDTO result = userService.updateProfile(1L, updateProfileDTO);

        // Then
        assertNotNull(result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testUpdateProfile_UserNotFound_ThrowsException() {
        // Given
        UpdateProfileDTO updateProfileDTO = new UpdateProfileDTO();
        updateProfileDTO.setName("Updated Name");
        updateProfileDTO.setUsername("updateduser");

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.updateProfile(1L, updateProfileDTO)
        );
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testUpdateProfile_UsernameAlreadyTaken_ThrowsException() {
        // Given
        UpdateProfileDTO updateProfileDTO = new UpdateProfileDTO();
        updateProfileDTO.setName("Updated Name");
        updateProfileDTO.setUsername("existinguser");

        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setUsername("existinguser");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findByUsername("existinguser")).thenReturn(anotherUser);

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.updateProfile(1L, updateProfileDTO)
        );
        assertEquals("Username is already taken", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void testDeleteAccount_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("correctPassword", "encodedPassword123")).thenReturn(true);
        doNothing().when(userRepository).delete(testUser);

        // When
        userService.deleteAccount(1L, "correctPassword");

        // Then
        verify(passwordEncoder, times(1)).matches("correctPassword", "encodedPassword123");
        verify(userRepository, times(1)).delete(testUser);
    }

    @Test
    void testDeleteAccount_UserNotFound_ThrowsException() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.deleteAccount(1L, "password")
        );
        assertEquals("User not found", exception.getMessage());
        verify(userRepository, never()).delete(any());
    }

    @Test
    void testDeleteAccount_IncorrectPassword_ThrowsException() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword123")).thenReturn(false);

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.deleteAccount(1L, "wrongPassword")
        );
        assertEquals("Password is incorrect", exception.getMessage());
        verify(userRepository, never()).delete(any());
    }

    @Test
    void testUpdateProfile_SameUsername_Success() {
        // Given
        UpdateProfileDTO updateProfileDTO = new UpdateProfileDTO();
        updateProfileDTO.setName("Updated Name");
        updateProfileDTO.setUsername("testuser"); // Same as current

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findByUsername("testuser")).thenReturn(testUser);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        UserDTO result = userService.updateProfile(1L, updateProfileDTO);

        // Then
        assertNotNull(result);
        verify(userRepository, times(1)).save(any(User.class));
    }
}
