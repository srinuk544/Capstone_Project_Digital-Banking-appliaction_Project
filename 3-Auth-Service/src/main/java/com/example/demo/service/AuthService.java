package com.example.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtUtil;

@Service
public class AuthService {

    private static final Logger log =
            LoggerFactory.getLogger(AuthService.class);

    private final UserRepository repo;
    private final PasswordEncoder encoder;

    public AuthService(UserRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    public String register(RegisterRequest request) {

        log.info("Register request received for user {}", request.username);

        if (repo.findByUsername(request.username).isPresent()) {
            log.warn("User already exists: {}", request.username);
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(request.username);
        user.setPassword(encoder.encode(request.password));
        user.setRole(request.role);

        repo.save(user);

        log.info("User registered successfully: {}", request.username);

        return "User registered successfully";
    }

    public String login(LoginRequest request) {

        log.info("Login attempt for user {}", request.username);

        User user = repo.findByUsername(request.username)
                .orElseThrow(() -> {
                    log.error("Invalid login attempt for user {}", request.username);
                    return new RuntimeException("Invalid credentials");
                });

        if (!encoder.matches(request.password, user.getPassword())) {
            log.error("Invalid password for user {}", request.username);
            throw new RuntimeException("Invalid credentials");
        }

        log.info("User authenticated successfully: {}", request.username);

        return JwtUtil.generateToken(user.getUsername(), user.getRole());
    }
}