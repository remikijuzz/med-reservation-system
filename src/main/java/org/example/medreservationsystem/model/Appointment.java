package org.example.medreservationsystem.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointment")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Pole z datą i godziną wizyty.
     * Mapuje się na kolumnę "date" typu TIMESTAMP w bazie danych.
     */
    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    // Jeśli chcesz obsługiwać status wizyty, odkomentuj poniższe:
    // @Column(name = "status")
    // private String status;
}
