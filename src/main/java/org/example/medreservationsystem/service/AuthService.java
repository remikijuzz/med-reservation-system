package org.example.medreservationsystem.service;

import lombok.RequiredArgsConstructor;
import org.example.medreservationsystem.model.User;
import org.example.medreservationsystem.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public User register(String username, String rawPassword) {
        if (userRepo.findByUsername(username).isPresent()) {
            throw new ResponseStatusException(CONFLICT, "Username already taken");
        }
        User u = new User();
        u.setUsername(username);
        u.setPassword(passwordEncoder.encode(rawPassword));
        u.setRoles(Set.of("ROLE_USER"));
        return userRepo.save(u);
    }
}
