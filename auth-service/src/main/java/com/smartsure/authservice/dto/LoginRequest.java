package com.smartsure.authservice.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
public class LoginRequest {

    @Email(message = "Invalid email")
    private String email;

    @NotBlank(message = "Password required")
    private String password;
}