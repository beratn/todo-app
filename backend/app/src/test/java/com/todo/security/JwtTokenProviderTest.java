package com.todo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

    @Spy
    private JwtTokenProvider jwtTokenProvider;

    private UserDetails userDetails;
    private static final String USERNAME = "testuser";
    private static final long JWT_EXPIRATION = 3600000; // 1 hour

    @BeforeEach
    void setUp() {
        userDetails = User.builder()
                .username(USERNAME)
                .password("password")
                .authorities(new ArrayList<>())
                .build();

        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpiration", JWT_EXPIRATION);
    }

    @Test
    void generateToken_Success() {
        // Act
        String token = jwtTokenProvider.generateToken(userDetails);

        // Assert
        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    void extractUsername_Success() {
        // Arrange
        String token = jwtTokenProvider.generateToken(userDetails);

        // Act
        String username = jwtTokenProvider.extractUsername(token);

        // Assert
        assertEquals(USERNAME, username);
    }

    @Test
    void extractExpiration_Success() {
        // Arrange
        String token = jwtTokenProvider.generateToken(userDetails);
        Date now = new Date();

        // Act
        Date expiration = jwtTokenProvider.extractExpiration(token);

        // Assert
        assertNotNull(expiration);
        assertTrue(expiration.after(now));
        assertTrue(expiration.getTime() - now.getTime() <= JWT_EXPIRATION);
    }

    @Test
    void extractClaim_Success() {
        // Arrange
        String token = jwtTokenProvider.generateToken(userDetails);

        // Act
        String subject = jwtTokenProvider.extractClaim(token, Claims::getSubject);
        Date issuedAt = jwtTokenProvider.extractClaim(token, Claims::getIssuedAt);
        Date expiration = jwtTokenProvider.extractClaim(token, Claims::getExpiration);

        // Assert
        assertEquals(USERNAME, subject);
        assertNotNull(issuedAt);
        assertNotNull(expiration);
        assertTrue(expiration.after(issuedAt));
    }

    @Test
    void validateToken_ValidToken_Success() {
        // Arrange
        String token = jwtTokenProvider.generateToken(userDetails);

        // Act
        boolean isValid = jwtTokenProvider.validateToken(token, userDetails);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void validateToken_ExpiredToken_Failure() {
        // Arrange
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpiration", 1); // 1ms expiration
        String token = jwtTokenProvider.generateToken(userDetails);
        
        // Wait for token to expire
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Act & Assert
        assertThrows(ExpiredJwtException.class, () -> {
            jwtTokenProvider.validateToken(token, userDetails);
        });
    }

    @Test
    void validateToken_InvalidUsername_Failure() {
        // Arrange
        String token = jwtTokenProvider.generateToken(userDetails);
        UserDetails differentUser = User.builder()
                .username("differentuser")
                .password("password")
                .authorities(new ArrayList<>())
                .build();

        // Act
        boolean isValid = jwtTokenProvider.validateToken(token, differentUser);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void isTokenValid_ValidToken_Success() {
        // Arrange
        String token = jwtTokenProvider.generateToken(userDetails);

        // Act
        boolean isValid = jwtTokenProvider.isTokenValid(token);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void isTokenValid_InvalidToken_Failure() {
        // Act
        boolean isValid = jwtTokenProvider.isTokenValid("invalid.token.here");

        // Assert
        assertFalse(isValid);
    }

    @Test
    void isTokenValid_ExpiredToken_Failure() {
        // Arrange
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpiration", 1); // 1ms expiration
        String token = jwtTokenProvider.generateToken(userDetails);
        
        // Wait for token to expire
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Act
        boolean isValid = jwtTokenProvider.isTokenValid(token);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void isTokenValid_NullToken_Failure() {
        // Act
        boolean isValid = jwtTokenProvider.isTokenValid(null);

        // Assert
        assertFalse(isValid);
    }
}
