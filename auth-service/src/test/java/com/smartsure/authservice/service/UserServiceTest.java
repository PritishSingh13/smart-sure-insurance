package com.smartsure.authservice.service;

import com.smartsure.authservice.model.RegisterRequest;
import com.smartsure.authservice.model.LoginRequest;
import com.smartsure.authservice.model.User;
import com.smartsure.authservice.repository.UserRepository;
import com.smartsure.authservice.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    // =========================
    // TEST 1: REGISTER SUCCESS
    // =========================
    @Test
    void testRegisterUserSuccess() {

        RegisterRequest request = new RegisterRequest();
        request.setName("John");
        request.setEmail("john@gmail.com");
        request.setPassword("1234");
        request.setPhone("9999999999");
        request.setAddress("India");
        request.setRole("CUSTOMER");

        when(userRepository.findByEmail("john@gmail.com"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode("1234"))
                .thenReturn("encoded1234");

        when(userRepository.save(any(User.class)))
                .thenReturn(new User());

        String result = authService.register(request);

        assertEquals("User registered successfully!", result);

        verify(userRepository, times(1)).save(any(User.class));
    }

    // =========================
    // TEST 2: USER ALREADY EXISTS
    // =========================
    @Test
    void testRegisterUserAlreadyExists() {

        RegisterRequest request = new RegisterRequest();
        request.setEmail("john@gmail.com");

        User existingUser = new User();
        existingUser.setEmail("john@gmail.com");

        when(userRepository.findByEmail("john@gmail.com"))
                .thenReturn(Optional.of(existingUser));

        String result = authService.register(request);

        assertEquals("User already exists!", result);

        verify(userRepository, never()).save(any(User.class));
    }

    // =========================
    // TEST 3: LOGIN SUCCESS
    // =========================
    @Test
    void testLoginSuccess() {

        LoginRequest request = new LoginRequest();
        request.setEmail("john@gmail.com");
        request.setPassword("1234");

        User user = new User();
        user.setEmail("john@gmail.com");
        user.setPassword("encoded1234");
        user.setRole("CUSTOMER");

        when(userRepository.findByEmail("john@gmail.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("1234", "encoded1234"))
                .thenReturn(true);

        when(jwtUtil.generateToken("john@gmail.com", "CUSTOMER"))
                .thenReturn("mock-token");

        var response = authService.login(request);

        assertNotNull(response);
        assertEquals("mock-token", response.getToken());
        assertEquals("Login successful", response.getMessage());
    }

    // =========================
    // TEST 4: LOGIN WRONG PASSWORD
    // =========================
    @Test
    void testLoginFailureWrongPassword() {

        LoginRequest request = new LoginRequest();
        request.setEmail("john@gmail.com");
        request.setPassword("wrongpass");

        User user = new User();
        user.setEmail("john@gmail.com");
        user.setPassword("encoded1234");

        when(userRepository.findByEmail("john@gmail.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("wrongpass", "encoded1234"))
                .thenReturn(false);

        Exception ex = assertThrows(RuntimeException.class, () -> {
            authService.login(request);
        });

        assertEquals("Invalid credentials", ex.getMessage());
    }

    // =========================
    // TEST 5: USER NOT FOUND
    // =========================
    @Test
    void testLoginUserNotFound() {

        LoginRequest request = new LoginRequest();
        request.setEmail("abc@gmail.com");
        request.setPassword("1234");

        when(userRepository.findByEmail("abc@gmail.com"))
                .thenReturn(Optional.empty());

        Exception ex = assertThrows(RuntimeException.class, () -> {
            authService.login(request);
        });

        assertEquals("User not found", ex.getMessage());
    }
}