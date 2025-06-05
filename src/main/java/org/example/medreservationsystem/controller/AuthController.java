package org.example.medreservationsystem.controller;

import org.example.medreservationsystem.model.User;
import org.example.medreservationsystem.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Rejestracja zwykłego użytkownika (ROLE_USER)
    @PostMapping("/register")
    public User registerUser(@RequestBody User user) {
        return authService.register(user);
    }

    // Rejestracja admina – dostępna tylko dla istniejącego ADMINA
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register-admin")
    public User registerAdmin(@RequestBody User user) {
        return authService.registerAdmin(user);
    }

    // Logowanie – zwraca prosty „dummy” token
    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody User user) {
        String token = authService.login(user);
        if (token == null) {
            return ResponseEntity.status(401).body("Nieprawidłowe dane logowania");
        }
        return ResponseEntity.ok(token);
    }
}
