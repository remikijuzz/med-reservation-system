// src/test/java/org/example/medreservationsystem/model/NotificationChannelTest.java
package org.example.medreservationsystem.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotificationChannelTest {

    @Test
    void values_containsEmailAndSms() {
        NotificationChannel[] vals = NotificationChannel.values();
        assertArrayEquals(
            new NotificationChannel[]{NotificationChannel.EMAIL, NotificationChannel.SMS},
            vals,
            "Enum should expose EMAIL and SMS"
        );
    }

    @Test
    void valueOf_email() {
        assertEquals(
            NotificationChannel.EMAIL,
            NotificationChannel.valueOf("EMAIL"),
            "valueOf(\"EMAIL\") should return EMAIL"
        );
    }

    @Test
    void valueOf_sms() {
        assertEquals(
            NotificationChannel.SMS,
            NotificationChannel.valueOf("SMS"),
            "valueOf(\"SMS\") should return SMS"
        );
    }

    @Test
    void nameAndOrdinal_areConsistent() {
        NotificationChannel e = NotificationChannel.EMAIL;
        assertEquals("EMAIL", e.name());
        assertEquals(0, e.ordinal());

        NotificationChannel s = NotificationChannel.SMS;
        assertEquals("SMS", s.name());
        assertEquals(1, s.ordinal());
    }
}
