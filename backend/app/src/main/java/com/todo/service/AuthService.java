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
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists");
        }

        var user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);
        var token = jwtTokenProvider.generateToken(user);
        return AuthResponse.builder()
                .token(token)
                .build();
    }

    public AuthResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        var token = jwtTokenProvider.generateToken(user);
        return AuthResponse.builder()
                .token(token)
                .build();
    }

    public void validateToken(String token) {
        try {
            jwtTokenProvider.extractUsername(token);
            if (!jwtTokenProvider.isTokenValid(token)) {
                throw new InvalidTokenException("Invalid token");
            }
        } catch (Exception e) {
            throw new InvalidTokenException("Invalid token");
        }
    }
}
