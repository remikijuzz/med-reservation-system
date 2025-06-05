package org.example.medreservationsystem.controller;

import org.example.medreservationsystem.model.User;
import org.example.medreservationsystem.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        User saved = authService.register(user);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/register-admin")
    public ResponseEntity<User> registerAdmin(@RequestBody User user) {
        User saved = authService.registerAdmin(user);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody User user) {
        String token = authService.login(user);
        if (token == null) {
            return ResponseEntity.status(401).body("Nieprawidłowe dane logowania");
        }
        return ResponseEntity.ok(token);
    }
}
