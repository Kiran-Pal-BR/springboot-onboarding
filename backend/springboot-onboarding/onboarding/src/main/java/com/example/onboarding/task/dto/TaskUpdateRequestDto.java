package com.example.onboarding.task.dto;
import jakarta.validation.constraints.NotBlank;

public record TaskUpdateRequestDto (
        @NotBlank(message = "Title cannot be blank")
        String title,
        String description,
        boolean completed
){}


