package org.example.medreservationsystem.service;

import org.example.medreservationsystem.model.Appointment;

/**
 * Interfejs strategii powiadomień.
 */
public interface NotificationService {
    /**
     * Wysyła powiadomienie o nowej wizycie.
     * @param appointment Obiekt Appointment, który został właśnie utworzony.
     */
    void sendNotification(Appointment appointment);
}
