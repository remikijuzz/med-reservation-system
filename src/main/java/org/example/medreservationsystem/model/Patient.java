package org.example.medreservationsystem.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "patient")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Patient extends User {

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(unique = true)
    private String email;

    private String phone;
}
