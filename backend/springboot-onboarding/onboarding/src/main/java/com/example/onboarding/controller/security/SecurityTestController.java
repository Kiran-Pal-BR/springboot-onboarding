package com.example.onboarding.controller.security;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class SecurityTestController {

    @GetMapping("/public/welcome")
    public String publicEndpoint() {
        return "Welcome! This is a public endpoint accessible to everyone.";
    }

    @GetMapping("/tasks/secure")
    public String protectedEndpoint() {
        return "Success! You have accessed a protected endpoint requiring authentication.";
    }

    @GetMapping("/admin/dashboard")
    public String adminEndpoint() {
        return "Admin Dashboard: Authorized access granted for ADMIN role only.";
    }
}
