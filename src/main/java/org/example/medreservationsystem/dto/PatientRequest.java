package org.example.medreservationsystem.dto;

import lombok.Data;
import org.example.medreservationsystem.model.NotificationChannel;

import javax.validation.constraints.NotBlank;

@Data
public class PatientRequest {
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
    private String phoneNumber;

    private NotificationChannel notificationChannel;
}
