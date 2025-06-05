package org.example.medreservationsystem.service;

import org.example.medreservationsystem.model.Appointment;

public interface NotificationService {
    void sendNotification(Appointment appointment);
}
