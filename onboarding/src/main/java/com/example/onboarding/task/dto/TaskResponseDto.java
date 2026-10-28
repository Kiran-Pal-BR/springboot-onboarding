package com.example.onboarding.task.dto;

public record TaskResponseDto(
        String id,
        String title,
        String description,
        boolean completed
) {}
