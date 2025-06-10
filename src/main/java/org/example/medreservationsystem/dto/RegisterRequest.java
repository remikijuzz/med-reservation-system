package org.example.medreservationsystem.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import org.example.medreservationsystem.model.NotificationChannel;

public class RegisterRequest {
    @NotBlank
    private String username;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;

    private NotificationChannel notificationChannel;

    public RegisterRequest() {}
    public RegisterRequest(String username, String email, String password, NotificationChannel notificationChannel) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.notificationChannel = notificationChannel;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public NotificationChannel getNotificationChannel() {
        return notificationChannel;
    }

    public void setNotificationChannel(NotificationChannel notificationChannel) {
        this.notificationChannel = notificationChannel;
    }
}
