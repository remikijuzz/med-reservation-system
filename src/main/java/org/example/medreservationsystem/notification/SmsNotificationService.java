package org.example.medreservationsystem.notification;

import org.example.medreservationsystem.model.User;
import org.springframework.stereotype.Service;

@Service("SMS")
public class SmsNotificationService implements NotificationService {

    @Override
    public void sendNotification(User user, String message) {
        // Integracja z SMS gateway w praktyce.
        System.out.println("Wysyłam SMS do " + user.getUsername() + " (tel: " + 
            // Zakładamy, że User ma pole phoneNumber w klasach dziedziczących
            // Trzeba rzutować do Patient lub Doctor, lub dodać wspólne pole phone w User, jeśli używasz tu.
            // Dla uproszczenia: załóżmy, że email pola phoneNumber są w Patient/Doctor
            message + ")");
        // W praktyce rzutowanie lub sprawdzenie instancji:
        String phone = null;
        if (user instanceof org.example.medreservationsystem.model.Patient) {
            phone = ((org.example.medreservationsystem.model.Patient) user).getPhoneNumber();
        } else if (user instanceof org.example.medreservationsystem.model.Doctor) {
            phone = ((org.example.medreservationsystem.model.Doctor) user).getPhoneNumber();
        }
        System.out.println("Treść SMS do " + phone + ": " + message);
    }
}
