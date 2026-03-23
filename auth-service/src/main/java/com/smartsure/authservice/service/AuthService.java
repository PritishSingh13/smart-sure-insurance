package com.smartsure.authservice.service;

import com.smartsure.authservice.model.*;
import com.smartsure.authservice.model.User;
import com.smartsure.authservice.repository.UserRepository;
import com.smartsure.authservice.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // REGISTER
    public String register(RegisterRequest request) {

        Optional<User> existing = userRepository.findByEmail(request.getEmail());
        if (existing.isPresent()) {
            return "User already exists!";
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());

        // 🔥 STEP 1 FIX: CLEAN ROLE HANDLING
        String role = (request.getRole() == null || request.getRole().isEmpty())
                ? "CUSTOMER"
                : request.getRole().trim().toUpperCase();

        user.setRole(role);

        userRepository.save(user);

        return "User registered successfully!";
    }

    // LOGIN
    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        // 🔥 STEP 1 FIX: CLEAN ROLE BEFORE JWT
        String role = user.getRole().trim().toUpperCase();

        String token = jwtUtil.generateToken(user.getEmail(), role);

        return new AuthResponse(token, "Login successful");
    }
}