package org.example.medreservationsystem.service;

import org.example.medreservationsystem.dto.LoginRequest;
import org.example.medreservationsystem.dto.RegisterRequest;
import org.example.medreservationsystem.model.Doctor;
import org.example.medreservationsystem.model.Patient;
import org.example.medreservationsystem.model.User;
import org.example.medreservationsystem.model.NotificationChannel;
import org.example.medreservationsystem.repository.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;     // zakładam, że masz serwis do JWT

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /** rejestruje zwykłego usera (ROLE_USER) */
    public User registerUser(RegisterRequest req) {
        ensureUniqueUsernameAndEmail(req.getUsername(), req.getEmail());

        User u = new User();
        u.setUsername(req.getUsername());
        u.setEmail(req.getEmail());
        u.setPassword(passwordEncoder.encode(req.getPassword()));
        u.getRoles().add(User.ROLE_USER);
        if (req.getNotificationChannel() != null) {
            u.setNotificationChannel(req.getNotificationChannel());
        } else {
            u.setNotificationChannel(NotificationChannel.EMAIL);
        }
        return userRepository.save(u);
    }

    /** rejestruje admina (ROLE_USER + ROLE_ADMIN) */
    public User registerAdmin(RegisterRequest req) {
        ensureUniqueUsernameAndEmail(req.getUsername(), req.getEmail());

        User u = new User();
        u.setUsername(req.getUsername());
        u.setEmail(req.getEmail());
        u.setPassword(passwordEncoder.encode(req.getPassword()));
        u.getRoles().add(User.ROLE_USER);
        u.getRoles().add(User.ROLE_ADMIN);
        if (req.getNotificationChannel() != null) {
            u.setNotificationChannel(req.getNotificationChannel());
        }
        return userRepository.save(u);
    }

    /** rejestracja pacjenta: DTO + parametry */
    public Patient registerPatient(RegisterRequest req,
                                   String firstName,
                                   String lastName,
                                   String phoneNumber) {
        ensureUniqueUsernameAndEmail(req.getUsername(), req.getEmail());

        Patient p = new Patient();
        p.setUsername(req.getUsername());
        p.setEmail(req.getEmail());
        p.setPassword(passwordEncoder.encode(req.getPassword()));
        p.getRoles().add(User.ROLE_PATIENT);
        p.setFirstName(firstName);
        p.setLastName(lastName);
        p.setPhoneNumber(phoneNumber);
        if (req.getNotificationChannel() != null) {
            p.setNotificationChannel(req.getNotificationChannel());
        }
        return userRepository.save(p);
    }

    /** rejestracja lekarza: DTO + parametry */
    public Doctor registerDoctor(RegisterRequest req,
                                 String firstName,
                                 String lastName,
                                 String specialization,
                                 String phoneNumber) {
        ensureUniqueUsernameAndEmail(req.getUsername(), req.getEmail());

        Doctor d = new Doctor();
        d.setUsername(req.getUsername());
        d.setEmail(req.getEmail());
        d.setPassword(passwordEncoder.encode(req.getPassword()));
        d.getRoles().add(User.ROLE_DOCTOR);
        d.setFirstName(firstName);
        d.setLastName(lastName);
        d.setSpecialization(specialization);
        d.setPhoneNumber(phoneNumber);
        if (req.getNotificationChannel() != null) {
            d.setNotificationChannel(req.getNotificationChannel());
        }
        return userRepository.save(d);
    }

    /** login zwracający token po walidacji hasła */
    public String login(LoginRequest req) {
        User u = userRepository.findByUsername(req.getUsername());
        if (u == null || !passwordEncoder.matches(req.getPassword(), u.getPassword())) {
            throw new BadCredentialsException("Nieprawidłowy login lub hasło");
        }
        return jwtService.generateToken(u);
    }

    // ---- pomocnicze ----

    private void ensureUniqueUsernameAndEmail(String username, String email) {
        if (userRepository.existsByUsername(username)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Nazwa użytkownika zajęta");
        }
        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email już zajęty");
        }
    }
}
