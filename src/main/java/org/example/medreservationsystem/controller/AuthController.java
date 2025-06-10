package org.example.medreservationsystem.controller;

import org.example.medreservationsystem.dto.LoginRequest;
import org.example.medreservationsystem.dto.RegisterRequest;
import org.example.medreservationsystem.dto.JwtResponse;
import org.example.medreservationsystem.model.Doctor;
import org.example.medreservationsystem.model.Patient;
import org.example.medreservationsystem.model.User;
import org.example.medreservationsystem.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Rejestracja podstawowego użytkownika (rola ROLE_USER).
     */
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@Valid @RequestBody RegisterRequest req) {
        User saved = authService.registerUser(req);
        // Nie zwracamy hasła w odpowiedzi
        saved.setPassword(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * Rejestracja pacjenta.
     * Przykład: POST /api/auth/register/patient?firstName=Anna&lastName=Kowalska&phoneNumber=987654321
     */
    @PostMapping("/register/patient")
    public ResponseEntity<Patient> registerPatient(
            @Valid @RequestBody RegisterRequest req,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String phoneNumber) {
        Patient saved = authService.registerPatient(req, firstName, lastName, phoneNumber);
        saved.setPassword(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * Rejestracja lekarza.
     * Przykład: POST /api/auth/register/doctor?firstName=John&lastName=Smith&specialization=Cardio&phoneNumber=12345
     */
    @PostMapping("/register/doctor")
    public ResponseEntity<Doctor> registerDoctor(
            @Valid @RequestBody RegisterRequest req,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String specialization,
            @RequestParam String phoneNumber) {
        Doctor saved = authService.registerDoctor(req, firstName, lastName, specialization, phoneNumber);
        saved.setPassword(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * Rejestracja administracyjna (rola ROLE_ADMIN).
     */
    @PostMapping("/register-admin")
    public ResponseEntity<User> registerAdmin(@Valid @RequestBody RegisterRequest req) {
        User saved = authService.registerAdmin(req);
        saved.setPassword(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * Login: zwraca JSON {"token":"..."} lub 401 Unauthorized przy błędnych danych.
     */
        @PostMapping("/login")
        public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest req) {
            try {
                // Wywołaj metodę serwisu, która przyjmuje LoginRequest i zwraca String token
                String token = authService.login(req);
                // Zwracamy obiekt JwtResponse z tokenem
                return ResponseEntity.ok(new JwtResponse(token));
            } catch (BadCredentialsException | UsernameNotFoundException ex) {
                // 401 Unauthorized przy niepoprawnych danych
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Nieprawidłowy login lub hasło"));
            } catch (Exception ex) {
                // Inne nieprzewidziane błędy → 500
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "Wystąpił błąd podczas logowania"));
            }
        }
    }