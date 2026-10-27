package com.example.onboarding.task;
import java.util.Objects;

public record Task (
    String id,
    String title,
    String description,
    boolean completed
){
    public  Task {
        Objects.requireNonNull(id,"Task id cannot be null");
        Objects.requireNonNull(title,"Task title cannot be null");
    }
}
