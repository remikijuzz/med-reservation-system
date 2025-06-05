package org.example.medreservationsystem.model;

import javax.persistence.*;

@Entity
@Table(name = "app_user")
public class User {

    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;      // tutaj przechowujemy hasło dosłownie

    @Column(nullable = false)
    private String role;

    public User() { }

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        setRole(role);
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    // Hasło ustawiamy dokładnie takim, jak je otrzymaliśmy w JSON-ie
    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        if (role == null) {
            this.role = ROLE_USER;
        } else if (!role.startsWith("ROLE_")) {
            this.role = "ROLE_" + role;
        } else {
            this.role = role;
        }
    }
}
