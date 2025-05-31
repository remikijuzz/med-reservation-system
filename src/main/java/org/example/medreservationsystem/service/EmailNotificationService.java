package org.example.medreservationsystem.service;

import org.example.medreservationsystem.model.Appointment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * Implementacja NotificationService wysyłająca „emailem”.
 */
@Service
@Primary
public class EmailNotificationService implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationService.class);

    @Override
    public void sendNotification(Appointment appointment) {
        // Tutaj w rzeczywistej aplikacji byłby kod wysyłający email.
        // Dla uproszczenia wypisujemy w logu:
        String msg = String.format(
            "Email: Wizyta zaplanowana na %s dla pacjenta [%s] u lekarza [%s].",
            appointment.getDate(),
            appointment.getPatient().getName(),
            appointment.getDoctor().getName()
        );
        log.info(msg);
    }
}
