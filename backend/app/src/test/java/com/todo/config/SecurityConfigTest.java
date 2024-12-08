package com.todo.config;

import com.todo.security.JwtAuthenticationFilter;
import com.todo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SecurityConfigTest {

    private SecurityConfig securityConfig;
    private UserService userService;
    private JwtAuthenticationFilter jwtAuthFilter;
    private AuthenticationConfiguration authConfig;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        jwtAuthFilter = mock(JwtAuthenticationFilter.class);
        authConfig = mock(AuthenticationConfiguration.class);
        
        securityConfig = new SecurityConfig(jwtAuthFilter, userService);
    }

    @Test
    void passwordEncoder_ShouldReturnBCryptPasswordEncoder() {
        // When
        PasswordEncoder encoder = securityConfig.passwordEncoder();
        
        // Then
        assertNotNull(encoder);
        assertTrue(encoder instanceof BCryptPasswordEncoder);
        String password = "testPassword";
        String encodedPassword = encoder.encode(password);
        assertTrue(encoder.matches(password, encodedPassword));
    }

    @Test
    void authenticationProvider_ShouldReturnConfiguredProvider() {
        // When
        AuthenticationProvider provider = securityConfig.authenticationProvider();
        
        // Then
        assertNotNull(provider);
        assertTrue(provider instanceof DaoAuthenticationProvider);
    }

    @Test
    void corsConfigurationSource_ShouldReturnProperConfiguration() {
        // Given
        HttpServletRequest request = new MockHttpServletRequest();
        
        // When
        CorsConfigurationSource source = securityConfig.corsConfigurationSource();
        CorsConfiguration config = source.getCorsConfiguration(request);
        
        // Then
        assertNotNull(config);
        assertTrue(config.getAllowedOrigins().contains("http://localhost:8080"));
        assertTrue(config.getAllowedOrigins().contains("http://localhost:3000"));
        assertTrue(config.getAllowedMethods().containsAll(java.util.Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")));
        assertTrue(config.getAllowedHeaders().containsAll(java.util.Arrays.asList("Authorization", "Content-Type")));
        assertTrue(config.getAllowCredentials());
    }

    @Test
    void authenticationManager_ShouldReturnAuthenticationManager() throws Exception {
        // Given
        AuthenticationManager expectedManager = mock(AuthenticationManager.class);
        when(authConfig.getAuthenticationManager()).thenReturn(expectedManager);

        // When
        AuthenticationManager actualManager = securityConfig.authenticationManager(authConfig);
        
        // Then
        assertNotNull(actualManager);
        assertEquals(expectedManager, actualManager);
        verify(authConfig).getAuthenticationManager();
    }
}
