package org.example.medreservationsystem.dto;

/**
 * Prosty DTO zwracany po logowaniu: zawiera token JWT.
 */
public class JwtResponse {
    private String token;

    public JwtResponse() {}

    public JwtResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
}
