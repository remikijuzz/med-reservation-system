package org.example.medreservationsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    // 1) definiujemy prostego użytkownika w pamięci
    @Bean
    public InMemoryUserDetailsManager userDetailsService(PasswordEncoder encoder) {
        var user = User.withUsername("user")
            .password(encoder.encode("password"))
            .roles("USER")
            .build();
        var admin = User.withUsername("admin")
            .password(encoder.encode("adminpass"))
            .roles("USER", "ADMIN")
            .build();
        return new InMemoryUserDetailsManager(user, admin);
    }

    // 2) Bean dla BCrypta
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 3) Główna konfiguracja zabezpieczeń
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
          .csrf(AbstractHttpConfigurer::disable)
          .authorizeHttpRequests(auth -> auth
            // pozwól na dostęp do Swagger UI i dokumentacji
            .requestMatchers(
              "/swagger-ui.html", 
              "/swagger-ui/**", 
              "/v3/api-docs/**"
            ).permitAll()
            // endpointy rejestracji (jak dodasz AuthController)
            .requestMatchers("/api/auth/**").permitAll()
            // wszystkie pozostałe /api/** wymagają uwierzytelnienia
            .requestMatchers("/api/**").authenticated()
            // statyczne zasoby też
            .anyRequest().permitAll()
          )
          // użyj HTTP Basic zamiast form login
          .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
