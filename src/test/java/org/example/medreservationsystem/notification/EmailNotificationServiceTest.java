// src/test/java/org/example/medreservationsystem/notification/EmailNotificationServiceTest.java
package org.example.medreservationsystem.notification;

import org.example.medreservationsystem.model.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class EmailNotificationServiceTest {
    private final ByteArrayOutputStream out = new ByteArrayOutputStream();
    private EmailNotificationService svc;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(out));
        svc = new EmailNotificationService();
    }

    @Test
    void sendNotification_printsCorrectMessage() {
        Patient p = new Patient();
        p.setEmail("a@b.com");
        svc.sendNotification(p, "hello");
        String log = out.toString();
        assertTrue(log.contains("Wysyłam e-mail do a@b.com: hello"));
    }
}
