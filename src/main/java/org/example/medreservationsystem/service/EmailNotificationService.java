package org.example.medreservationsystem.service;

import org.example.medreservationsystem.model.Appointment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * Implementacja NotificationService wysyłająca „Email”.
 */
@Service
@Primary
public class EmailNotificationService implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationService.class);

    @Override
    public void sendNotification(Appointment appointment) {
        String msg = String.format(
            "Email: Wizyta zaplanowana na %s dla pacjenta [%s %s] u lekarza [%s %s].",
            appointment.getDate(),
            appointment.getPatient().getFirstName(),
            appointment.getPatient().getLastName(),
            appointment.getDoctor().getFirstName(),
            appointment.getDoctor().getLastName()
        );
        log.info(msg);
    }
}
