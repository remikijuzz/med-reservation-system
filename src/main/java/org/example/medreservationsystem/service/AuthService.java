package org.example.medreservationsystem.service;

import org.example.medreservationsystem.model.User;
import org.example.medreservationsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(User.ROLE_USER);   // domyślnie ROLE_USER
        return userRepository.save(user);
    }

    public User registerAdmin(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(User.ROLE_ADMIN);  // wymuszamy ROLE_ADMIN
        return userRepository.save(user);
    }

    public String login(User user) {
        User existing = userRepository.findByUsername(user.getUsername());
        if (existing != null && passwordEncoder.matches(user.getPassword(), existing.getPassword())) {
            // Na potrzeby projektu: zwracamy „dummy-token-<id>”
            return "dummy-token-" + existing.getId();
        }
        return null;
    }
}
