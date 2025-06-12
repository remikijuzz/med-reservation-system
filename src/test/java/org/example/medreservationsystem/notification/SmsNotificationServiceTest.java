// src/test/java/org/example/medreservationsystem/notification/SmsNotificationServiceTest.java
package org.example.medreservationsystem.notification;

import org.example.medreservationsystem.model.Doctor;
import org.example.medreservationsystem.model.Patient;
import org.example.medreservationsystem.model.NotificationChannel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class SmsNotificationServiceTest {
    private final ByteArrayOutputStream out = new ByteArrayOutputStream();
    private SmsNotificationService svc;

    @BeforeEach
    void setUp() {
        // Przechwycamy System.out
        System.setOut(new PrintStream(out));
        svc = new SmsNotificationService();
    }

    @AfterEach
    void tearDown() {
        // Przywracamy domyślne System.out
        System.setOut(System.out);
    }

    @Test
    void sendNotification_withPatient_printsCorrectSms() {
        // Przygotuj pacjenta z numerem telefonu
        Patient p = new Patient(
            "patient1",
            "patient1@example.com",
            "pw",
            "Alice",
            "Wonder",
            "+48123456789",
            NotificationChannel.SMS
        );

        svc.sendNotification(p, "hello");

        String log = out.toString().trim();
        // Sprawdźmy, że obie linie zostały wypisane
        assertTrue(log.contains("Wysyłam SMS do patient1"), 
            "Powinno zawierać część 'Wysyłam SMS do patient1'");
        assertTrue(log.contains("Treść SMS do +48123456789: hello"),
            "Powinno zawierać część 'Treść SMS do +48123456789: hello'");
    }

    @Test
    void sendNotification_withDoctor_printsCorrectSms() {
        // Przygotuj lekarza z numerem telefonu
        Doctor d = new Doctor(
            "doctorX",
            "doctorx@example.com",
            "pw",
            "John",
            "Doe",
            "Cardio",
            "+48987654321",
            NotificationChannel.SMS
        );

        svc.sendNotification(d, "testmsg");

        String log = out.toString().trim();
        assertTrue(log.contains("Wysyłam SMS do doctorX"),
            "Powinno zawierać część 'Wysyłam SMS do doctorX'");
        assertTrue(log.contains("Treść SMS do +48987654321: testmsg"),
            "Powinno zawierać część 'Treść SMS do +48987654321: testmsg'");
    }
}
