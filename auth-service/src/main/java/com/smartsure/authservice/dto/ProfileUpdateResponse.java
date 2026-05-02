package com.smartsure.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileUpdateResponse {
    private String token;
    private String role;
    private String email;
    private UserProfileDto user;
}
