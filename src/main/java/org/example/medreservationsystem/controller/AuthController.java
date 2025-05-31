package org.example.medreservationsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.example.medreservationsystem.model.User;
import org.example.medreservationsystem.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth", description = "User registration & login")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "Register a new user")
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegisterRequest req) {
        User created = authService.register(req.getUsername(), req.getPassword());
        return ResponseEntity.status(201).body(created);
    }

    @Operation(summary = "Login (use HTTP Basic or form login)")
    @GetMapping("/login")
    public ResponseEntity<String> login() {
        // punkt końcowy do testowania po uwierzytelnieniu Basic
        return ResponseEntity.ok("Logged in as: " + 
            org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getName()
        );
    }

    @Data
    static class RegisterRequest {
        private String username;
        private String password;
    }
}
