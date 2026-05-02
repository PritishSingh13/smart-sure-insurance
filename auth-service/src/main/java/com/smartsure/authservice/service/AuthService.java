package com.smartsure.authservice.service;

import com.smartsure.authservice.dto.*;
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

    // =========================
    // REGISTER
    // =========================
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

        String role = (request.getRole() == null || request.getRole().isEmpty())
                ? "CUSTOMER"
                : request.getRole().trim().toUpperCase();

        user.setRole(role);

        userRepository.save(user);

        return "User registered successfully!";
    }

    // =========================
    // LOGIN
    // =========================
    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String role = user.getRole().trim().toUpperCase();

        String token = jwtUtil.generateToken(user.getEmail(), role);

        return new AuthResponse(token, role, user.getEmail());
    }

    // =========================
    // GET ALL USERS
    // =========================
    public java.util.List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public UserProfileDto getUserProfileByEmail(String email) {
        return toUserProfile(getUserByEmail(email));
    }

    public java.util.List<UserProfileDto> getAllUserProfiles() {
        return userRepository.findAll()
                .stream()
                .map(this::toUserProfile)
                .toList();
    }

    public ProfileUpdateResponse updateProfile(String currentEmail, UpdateProfileRequest request) {
        User user = getUserByEmail(currentEmail);

        String nextEmail = request.getEmail().trim().toLowerCase();
        userRepository.findByEmail(nextEmail)
                .filter(existing -> !existing.getId().equals(user.getId()))
                .ifPresent(existing -> {
                    throw new RuntimeException("Email already exists");
                });

        user.setName(request.getName().trim());
        user.setEmail(nextEmail);
        user.setPhone(request.getPhone().trim());
        user.setAddress(request.getAddress().trim());
        user.setProfileImage(request.getProfileImage());

        User savedUser = userRepository.save(user);
        String role = savedUser.getRole().trim().toUpperCase();
        String token = jwtUtil.generateToken(savedUser.getEmail(), role);

        return new ProfileUpdateResponse(
                token,
                role,
                savedUser.getEmail(),
                toUserProfile(savedUser)
        );
    }

    private UserProfileDto toUserProfile(User user) {
        return new UserProfileDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getAddress(),
                user.getRole(),
                user.getProfileImage()
        );
    }
}
