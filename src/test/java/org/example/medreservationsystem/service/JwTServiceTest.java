// src/test/java/org/example/medreservationsystem/service/JwtServiceTest.java

package org.example.medreservationsystem.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.example.medreservationsystem.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private Key signingKey;
    private final String base64Secret = Base64.getEncoder()
        .encodeToString("01234567890123456789012345678901".getBytes()); // 32-byte key
    
    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        // inject our test secret and expiration
        ReflectionTestUtils.setField(jwtService, "jwtSecretBase64", base64Secret);
        ReflectionTestUtils.setField(jwtService, "jwtExpirationMs", 60000L);
        jwtService.init();
        signingKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(base64Secret));
    }

    @Test
    void generateToken_containsSubjectAndRoles() {
        // given
        User user = new User();
        user.setUsername("alice");
        user.getRoles().add("ROLE_USER");
        user.getRoles().add("ROLE_PATIENT");

        // when
        String token = jwtService.generateToken(user);

        // then
        assertNotNull(token);
        // parse it to inspect claims
        Jws<Claims> parsed = Jwts.parserBuilder()
            .setSigningKey(signingKey)
            .build()
            .parseClaimsJws(token);

        Claims body = parsed.getBody();
        assertEquals("alice", body.getSubject());
        // roles claim should be a list containing our roles
        @SuppressWarnings("unchecked")
        var roles = (java.util.List<String>) body.get("roles");
        assertTrue(roles.contains("ROLE_USER"));
        assertTrue(roles.contains("ROLE_PATIENT"));
    }

    @Test
    void generateToken_setsExpiration() {
        User user = new User();
        user.setUsername("bob");
        user.getRoles().add("ROLE_USER");

        long before = System.currentTimeMillis();
        String token = jwtService.generateToken(user);
        Jws<Claims> parsed = Jwts.parserBuilder()
            .setSigningKey(signingKey)
            .build()
            .parseClaimsJws(token);

        Date exp = parsed.getBody().getExpiration();
        assertNotNull(exp);
        assertTrue(exp.getTime() >= before + 59000, "Expiration should be at least ~1min ahead");
    }
}
