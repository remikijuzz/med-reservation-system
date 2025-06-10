// src/main/java/org/example/medreservationsystem/dto/UserResponse.java
package org.example.medreservationsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private Set<String> roles;
}
