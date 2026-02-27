package com.example.demo.service;

import com.example.demo.dto.LoginRequest;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private UserRepository repo;

    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private AuthService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLogin_Success() {

        User user = new User();
        user.setUsername("srinu");
        user.setPassword("encodedPassword");

        when(repo.findByUsername("srinu"))
                .thenReturn(Optional.of(user));

        when(encoder.matches("1234", "encodedPassword"))
                .thenReturn(true);

        LoginRequest request = new LoginRequest();
        request.username = "srinu";
        request.password = "1234";

        String token = service.login(request);

        assertNotNull(token);
    }

    @Test
    void testLogin_InvalidPassword() {

        User user = new User();
        user.setUsername("srinu");
        user.setPassword("encodedPassword");

        when(repo.findByUsername("srinu"))
                .thenReturn(Optional.of(user));

        when(encoder.matches("wrong", "encodedPassword"))
                .thenReturn(false);

        LoginRequest request = new LoginRequest();
        request.username = "srinu";
        request.password = "wrong";

        assertThrows(RuntimeException.class,
                () -> service.login(request));
    }
}