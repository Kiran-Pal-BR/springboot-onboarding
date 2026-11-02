package com.example.onboarding.project.dto;
import jakarta.validation.constraints.NotBlank;

public record ProjectUpdateRequestDto (
    @NotBlank(message = "Name cannot be blank")
    String name,
    String description
){}
