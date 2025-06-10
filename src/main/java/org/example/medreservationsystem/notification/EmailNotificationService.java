package org.example.medreservationsystem.notification;

import org.example.medreservationsystem.model.User;
import org.springframework.stereotype.Service;

@Service("EMAIL")
public class EmailNotificationService implements NotificationService {

    @Override
    public void sendNotification(User user, String message) {
        // Tutaj np. integracja z rzeczywistym serwisem mailowym.
        // Na potrzeby demo wypisujemy log:
        System.out.println("Wysyłam e-mail do " + user.getEmail() + ": " + message);
        // Można użyć JavaMailSender, itp. W praktyce wstrzyknij Bean JavaMailSender.
    }
}
