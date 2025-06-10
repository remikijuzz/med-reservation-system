package org.example.medreservationsystem.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "doctors")
@DiscriminatorValue("Doctor")
@PrimaryKeyJoinColumn(name = "id")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Doctor extends User {

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "specialization", nullable = false)
    private String specialization;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    // Konstruktor pomocniczy (opcjonalny)
    public Doctor(String username, String email, String password,
                  String firstName, String lastName, String specialization, String phoneNumber,
                  NotificationChannel notificationChannel) {
        super();
        setUsername(username);
        setEmail(email);
        setPassword(password);
        getRoles().add(User.ROLE_DOCTOR);
        setNotificationChannel(notificationChannel);
        this.firstName = firstName;
        this.lastName = lastName;
        this.specialization = specialization;
        this.phoneNumber = phoneNumber;
    }
}
