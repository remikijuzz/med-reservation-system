package org.example.medreservationsystem.dto;

import lombok.Data;
import org.example.medreservationsystem.model.NotificationChannel;

import javax.validation.constraints.NotBlank;

@Data
public class DoctorRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String email;
    @NotBlank
    private String password;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @NotBlank
    private String specialization;
    @NotBlank
    private String phoneNumber;

    // preferencja powiadomień
    private NotificationChannel notificationChannel;
}
