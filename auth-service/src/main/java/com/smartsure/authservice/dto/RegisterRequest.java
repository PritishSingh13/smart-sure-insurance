package com.smartsure.authservice.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
public class RegisterRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @Pattern(regexp = "\\d{10}", message = "Phone must be 10 digits")
    private String phone;

    private String address;

    @NotBlank(message = "Role is required")
    private String role;
}