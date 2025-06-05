package org.example.medreservationsystem.service;

import org.example.medreservationsystem.model.User;
import org.example.medreservationsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;

    @Autowired
    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Rejestracja zwykłego użytkownika (hasło zapisywane jawnie, rolę ustawiamy na ROLE_USER).
     */
    public User register(User user) {
        user.setRole(User.ROLE_USER);   // domyślnie ROLE_USER
        return userRepository.save(user);
    }

    /**
     * Rejestracja ADMINA – jawne hasło, wymuszona rola ROLE_ADMIN.
     * (End­point chroniony @PreAuthorize("hasRole('ADMIN')") w AuthController)
     */
    public User registerAdmin(User user) {
        user.setRole(User.ROLE_ADMIN);
        return userRepository.save(user);
    }

    /**
     * Logowanie – porównujemy hasło „wprost” zamiast bcrypta.
     * Jeżeli login i hasło pokrywają się z tym, co jest w bazie, zwracamy „dummy-token-<id>”,
     * w przeciwnym razie zwracamy null (co przekłada się na 401).
     */
    public String login(User user) {
        User existing = userRepository.findByUsername(user.getUsername());
        if (existing != null && user.getPassword().equals(existing.getPassword())) {
            return "dummy-token-" + existing.getId();
        }
        return null;
    }
}
