package com.example.onboarding.project.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProjectCreateRequestDto (
    @NotBlank(message = "Project name cannot be blank")
    @Size(min = 3 , max = 100, message = "Name must be between 3 and 100 characters" )
    String name,
    String description
){}
