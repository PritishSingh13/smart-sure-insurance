package com.smartsure.policyservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Policy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Policy name cannot be empty")
    private String policyName;

    @NotBlank(message = "Policy type cannot be empty")
    private String policyType;

    @Positive(message = "Premium must be greater than 0")
    private Double premium;

    @Positive(message = "Duration must be greater than 0")
    private Integer duration;
}