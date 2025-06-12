// src/main/java/org/example/medreservationsystem/model/Patient.java
package org.example.medreservationsystem.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "patients")
@DiscriminatorValue("Patient")
@PrimaryKeyJoinColumn(name = "id")
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class Patient extends User {

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    // Konstruktor pomocniczy (opcjonalnie)
    public Patient(String username, String email, String password,
                   String firstName, String lastName, String phoneNumber,
                   NotificationChannel notificationChannel) {
        super();
        setUsername(username);
        setEmail(email);
        setPassword(password);
        getRoles().add(User.ROLE_PATIENT);
        setNotificationChannel(notificationChannel);
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
    }
}
