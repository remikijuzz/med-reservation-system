package org.example.medreservationsystem.controller;

import org.example.medreservationsystem.model.User;
import org.example.medreservationsystem.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        User saved = authService.register(user);
        // testy oczekują status 201 Created
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PostMapping("/register-admin")
    public ResponseEntity<User> registerAdmin(@RequestBody User user) {
        User saved = authService.registerAdmin(user);
        // testy oczekują status 201 Created
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody User user) {
        String token = authService.login(user);
        if (token == null) {
            // testy oczekują 401 Unauthorized przy niepoprawnych danych
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Nieprawidłowe dane logowania");
        }
        return ResponseEntity.ok(token);
    }
}
