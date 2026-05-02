package com.smartsure.authservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateProfileRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Invalid email format")
    private String email;

    @Pattern(regexp = "(\\d{10}|\\+\\d{1,4}\\s?\\d{6,14})", message = "Phone must include a valid number")
    private String phone;

    @NotBlank(message = "Address is required")
    private String address;

    private String profileImage;
}
