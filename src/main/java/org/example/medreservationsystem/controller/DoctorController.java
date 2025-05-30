package org.example.medreservationsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.medreservationsystem.model.Doctor;
import org.example.medreservationsystem.service.DoctorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Doctors", description = "Operations about doctors")
@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {
    private final DoctorService service;

    @Operation(summary = "Get all doctors")
    @GetMapping
    public List<Doctor> getAll() {
        return service.findAll();
    }

    @Operation(summary = "Get a doctor by ID")
    @GetMapping("/{id}")
    public Doctor getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @Operation(summary = "Create a new doctor")
    @PostMapping
    public ResponseEntity<Doctor> create(@RequestBody Doctor doctor) {
        Doctor created = service.create(doctor);
        return ResponseEntity.status(201).body(created);
    }

    @Operation(summary = "Update an existing doctor")
    @PutMapping("/{id}")
    public Doctor update(@PathVariable Long id, @RequestBody Doctor doctor) {
        return service.update(id, doctor);
    }

    @Operation(summary = "Delete a doctor by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
