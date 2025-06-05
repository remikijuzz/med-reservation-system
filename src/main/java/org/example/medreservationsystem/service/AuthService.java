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

    public User register(User user) {
        user.setRole(User.ROLE_USER);
        return userRepository.save(user);
    }

    public User registerAdmin(User user) {
        user.setRole(User.ROLE_ADMIN);
        return userRepository.save(user);
    }

    public String login(User user) {
        User existing = userRepository.findByUsername(user.getUsername());
        if (existing != null && user.getPassword().equals(existing.getPassword())) {
            return "dummy-token-" + existing.getId();
        }
        return null;
    }
}
