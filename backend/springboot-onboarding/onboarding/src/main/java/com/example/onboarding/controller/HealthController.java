package com.example.onboarding.controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.time.LocalDateTime;


@RestController
@RequestMapping("/api")


public class HealthController {
    @GetMapping("/health")
    public Map<String,Object> checkHealth(){
        return Map.of(
                "status" , "UP",
                "service", "SpringBoot Onboarding API",
                "timestamp" , LocalDateTime.now().toString()
        );
    }
}
