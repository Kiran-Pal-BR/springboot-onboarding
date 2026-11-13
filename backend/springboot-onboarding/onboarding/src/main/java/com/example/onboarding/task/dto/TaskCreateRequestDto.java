package com.example.onboarding.task.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TaskCreateRequestDto (
        @NotBlank(message =  "Task title cannot be blank")
        @Size(min = 3 , max = 100, message = "Title must be between 3 and 100 characters" )
        String title,
        String description,

        @NotBlank(message = "Project ID is required")
        String projectId
){}

