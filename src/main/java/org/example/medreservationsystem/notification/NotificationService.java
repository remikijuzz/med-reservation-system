package org.example.medreservationsystem.notification;

import org.example.medreservationsystem.model.User;

public interface NotificationService {
    /**
     * Wysyła powiadomienie do użytkownika z treścią message.
     */
    void sendNotification(User user, String message);
}
