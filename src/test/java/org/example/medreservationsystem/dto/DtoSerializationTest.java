// src/test/java/org/example/medreservationsystem/dto/DtoSerializationTest.java
package org.example.medreservationsystem.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.medreservationsystem.model.NotificationChannel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.validation.*;
import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DtoSerializationTest {

    private static ObjectMapper MAPPER;
    private static Validator VALIDATOR;

    @BeforeAll
    static void setUp() {
        MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule()); // for LocalDateTime
        ValidatorFactory vf = Validation.buildDefaultValidatorFactory();
        VALIDATOR = vf.getValidator();
    }

    @Test
    void appointmentRequest_jsonRoundTrip_andValidation() throws Exception {
        AppointmentRequest dto = new AppointmentRequest();
        dto.setPatientId(10L);
        dto.setDoctorId(20L);
        dto.setAppointmentDateTime(LocalDateTime.of(2025,6,30,14,15));
        dto.setDescription("check-up");

        String json = MAPPER.writeValueAsString(dto);
        AppointmentRequest back = MAPPER.readValue(json, AppointmentRequest.class);

        assertEquals(dto.getPatientId(),    back.getPatientId());
        assertEquals(dto.getDoctorId(),     back.getDoctorId());
        assertEquals(dto.getDescription(),  back.getDescription());
        assertEquals(dto.getAppointmentDateTime(), back.getAppointmentDateTime());

        Set<ConstraintViolation<AppointmentRequest>> errs =
            VALIDATOR.validate(back);
        assertTrue(errs.isEmpty());
    }

    @Test
    void doctorRequest_jsonRoundTrip_andValidation() throws Exception {
        DoctorRequest dto = new DoctorRequest();
        dto.setUsername("drx");
        dto.setEmail("drx@ex.pl");
        dto.setPassword("pw");
        dto.setFirstName("X");
        dto.setLastName("Y");
        dto.setSpecialization("Spec");
        dto.setPhoneNumber("123");
        dto.setNotificationChannel(NotificationChannel.SMS);

        String json = MAPPER.writeValueAsString(dto);
        DoctorRequest back = MAPPER.readValue(json, DoctorRequest.class);

        assertEquals(dto.getUsername(),       back.getUsername());
        assertEquals(dto.getEmail(),          back.getEmail());
        assertEquals(dto.getFirstName(),      back.getFirstName());
        assertEquals(dto.getNotificationChannel(), back.getNotificationChannel());

        Set<ConstraintViolation<DoctorRequest>> errs =
            VALIDATOR.validate(back);
        assertTrue(errs.isEmpty());
    }

    @Test
    void patientRequest_jsonRoundTrip_andValidation() throws Exception {
        PatientRequest dto = new PatientRequest();
        dto.setUsername("pat1");
        dto.setEmail("pat1@ex.pl");
        dto.setPassword("pw1");
        dto.setFirstName("PA");
        dto.setLastName("TB");
        dto.setPhoneNumber("321");
        dto.setNotificationChannel(NotificationChannel.EMAIL);

        String json = MAPPER.writeValueAsString(dto);
        PatientRequest back = MAPPER.readValue(json, PatientRequest.class);

        assertEquals(dto.getUsername(), back.getUsername());
        assertEquals(dto.getPhoneNumber(), back.getPhoneNumber());

        Set<ConstraintViolation<PatientRequest>> errs =
            VALIDATOR.validate(back);
        assertTrue(errs.isEmpty());
    }

    @Test
    void loginRequest_jsonRoundTrip_andValidation() throws Exception {
        LoginRequest dto = new LoginRequest();
        dto.setUsername("u1");
        dto.setPassword("secret");

        String json = MAPPER.writeValueAsString(dto);
        LoginRequest back = MAPPER.readValue(json, LoginRequest.class);

        assertEquals(dto.getUsername(), back.getUsername());
        assertEquals(dto.getPassword(), back.getPassword());

        Set<ConstraintViolation<LoginRequest>> errs =
            VALIDATOR.validate(back);
        assertTrue(errs.isEmpty());
    }

    @Test
    void registerRequest_jsonRoundTrip_andValidation() throws Exception {
        RegisterRequest dto = new RegisterRequest();
        dto.setUsername("r1");
        dto.setEmail("r1@ex.pl");
        dto.setPassword("pw");
        dto.setNotificationChannel(NotificationChannel.SMS);

        String json = MAPPER.writeValueAsString(dto);
        RegisterRequest back = MAPPER.readValue(json, RegisterRequest.class);

        assertEquals(dto.getEmail(), back.getEmail());
        assertEquals(dto.getNotificationChannel(), back.getNotificationChannel());

        Set<ConstraintViolation<RegisterRequest>> errs =
            VALIDATOR.validate(back);
        assertTrue(errs.isEmpty());
    }

    @Test
    void jwtResponse_and_userResponse_jsonSerializationOnly() throws Exception {
        // test JwtResponse round-trip
        JwtResponse jwt = new JwtResponse("the-token");
        String j1 = MAPPER.writeValueAsString(jwt);
        JwtResponse jwtBack = MAPPER.readValue(j1, JwtResponse.class);
        assertEquals("the-token", jwtBack.getToken());

        // test UserResponse serialization only
        UserResponse ur = new UserResponse(
            5L, "u5", "u5@ex.pl", Set.of("ROLE_USER","ROLE_PATIENT")
        );
        String j2 = MAPPER.writeValueAsString(ur);

        // assertions on JSON content instead of deserialization
        assertTrue(j2.contains("\"id\":5"));
        assertTrue(j2.contains("\"username\":\"u5\""));
        assertTrue(j2.contains("\"email\":\"u5@ex.pl\""));
        assertTrue(j2.contains("\"roles\""));
    }
}
