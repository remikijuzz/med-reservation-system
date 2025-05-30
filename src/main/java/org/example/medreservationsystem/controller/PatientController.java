package org.example.medreservationsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.medreservationsystem.model.Patient;
import org.example.medreservationsystem.service.PatientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Patients", description = "Operations about patients")
@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {
    private final PatientService service;

    @Operation(summary = "Get all patients")
    @GetMapping
    public List<Patient> getAll() {
        return service.findAll();
    }

    @Operation(summary = "Get a patient by ID")
    @GetMapping("/{id}")
    public Patient getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @Operation(summary = "Create a new patient")
    @PostMapping
    public ResponseEntity<Patient> create(@RequestBody Patient patient) {
        Patient created = service.create(patient);
        return ResponseEntity.status(201).body(created);
    }

    @Operation(summary = "Update an existing patient")
    @PutMapping("/{id}")
    public Patient update(@PathVariable Long id, @RequestBody Patient patient) {
        return service.update(id, patient);
    }

    @Operation(summary = "Delete patient by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
