package org.example.medreservationsystem.service;

import org.example.medreservationsystem.model.Appointment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Implementacja NotificationService wysyłająca „SMS-em”.
 */
@Service
public class SmsNotificationService implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(SmsNotificationService.class);

    @Override
    public void sendNotification(Appointment appointment) {
        // W rzeczywistości tu byłby kod do wysyłki SMS.
        // Dla uproszczenia wypisujemy w logu:
        String msg = String.format(
            "SMS: Nowa wizyta %s dla pacjenta [%s] u lekarza [%s].",
            appointment.getDate(),
            appointment.getPatient().getName(),
            appointment.getDoctor().getName()
        );
        log.info(msg);
    }
}
