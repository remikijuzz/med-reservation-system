package org.example.medreservationsystem.service;

import org.example.medreservationsystem.model.Appointment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SmsNotificationService implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(SmsNotificationService.class);

    @Override
    public void sendNotification(Appointment appointment) {
        String msg = String.format(
            "SMS: Nowa wizyta %s dla pacjenta [%s %s] u lekarza [%s %s].",
            appointment.getDate(),  // w encji jest getDate()
            appointment.getPatient().getFirstName(),
            appointment.getPatient().getLastName(),
            appointment.getDoctor().getFirstName(),
            appointment.getDoctor().getLastName()
        );
        log.info(msg);
    }
}
