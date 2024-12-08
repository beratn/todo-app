package com.todo.service;

import com.todo.dto.AuthRequest;
import com.todo.dto.AuthResponse;
import com.todo.dto.RegisterRequest;
import com.todo.exception.InvalidTokenException;
import com.todo.exception.UserAlreadyExistsException;
import com.todo.exception.UserNotFoundException;
import com.todo.model.User;
import com.todo.repository.UserRepository;
import com.todo.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private AuthRequest authRequest;
    private User user;
    private static final String TOKEN = "test.jwt.token";

    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .build();

        authRequest = AuthRequest.builder()
                .username("testuser")
                .password("password123")
                .build();

        user = User.builder()
                .id("1")
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .build();
    }

    @Test
    void register_Success() {
        // Arrange
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtTokenProvider.generateToken(any(User.class))).thenReturn(TOKEN);

        // Act
        AuthResponse response = authService.register(registerRequest);

        // Assert
        assertNotNull(response);
        assertEquals(TOKEN, response.getToken());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_UsernameExists_ThrowsException() {
        // Arrange
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(UserAlreadyExistsException.class,
                () -> authService.register(registerRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_EmailExists_ThrowsException() {
        // Arrange
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(UserAlreadyExistsException.class,
                () -> authService.register(registerRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void authenticate_Success() {
        // Arrange
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(jwtTokenProvider.generateToken(any(User.class))).thenReturn(TOKEN);

        // Act
        AuthResponse response = authService.authenticate(authRequest);

        // Assert
        assertNotNull(response);
        assertEquals(TOKEN, response.getToken());
        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        );
    }

    @Test
    void authenticate_UserNotFound_ThrowsException() {
        // Arrange
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class,
                () -> authService.authenticate(authRequest));
    }

    @Test
    void validateToken_ValidToken_Success() {
        // Arrange
        when(jwtTokenProvider.extractUsername(TOKEN)).thenReturn("testuser");
        when(jwtTokenProvider.isTokenValid(TOKEN)).thenReturn(true);

        // Act & Assert
        assertDoesNotThrow(() -> authService.validateToken(TOKEN));
    }

    @Test
    void validateToken_InvalidToken_ThrowsException() {
        // Arrange
        when(jwtTokenProvider.extractUsername(TOKEN)).thenReturn("testuser");
        when(jwtTokenProvider.isTokenValid(TOKEN)).thenReturn(false);

        // Act & Assert
        assertThrows(InvalidTokenException.class,
                () -> authService.validateToken(TOKEN));
    }

    @Test
    void validateToken_TokenExtractionFails_ThrowsException() {
        // Arrange
        when(jwtTokenProvider.extractUsername(TOKEN)).thenThrow(new RuntimeException());

        // Act & Assert
        assertThrows(InvalidTokenException.class,
                () -> authService.validateToken(TOKEN));
    }
}
