package org.example.medreservationsystem.config;

import org.example.medreservationsystem.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder auth = http.getSharedObject(AuthenticationManagerBuilder.class);
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        return auth.build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Wyłącz CSRF (na potrzeby H2 i Swagger UI)
            .csrf().disable()

            // Zezwól na wyświetlanie H2 Console w ramkach
            .headers().frameOptions().disable()
            .and()

            // Konfiguracja dostępów
            .authorizeHttpRequests(authz -> authz
                // 1. Rejestracja i logowanie otwarte
                .antMatchers("/api/auth/**").permitAll()

                // 2. H2 Console – zezwól wszystkim
                .antMatchers("/h2-console/**").permitAll()

                // 3. Swagger UI i OpenAPI endpoints – otwarte
                .antMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()

                // 4. Lekarze: tylko ADMIN może tworzyć/modyfikować/usuwać; każdy zalogowany może pobrać
                .antMatchers(HttpMethod.GET,    "/api/doctors/**").hasAnyRole("USER", "ADMIN")
                .antMatchers(HttpMethod.POST,   "/api/doctors/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.PUT,    "/api/doctors/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/api/doctors/**").hasRole("ADMIN")

                // 5. Pacjenci: tylko ADMIN może CRUD; każdy zalogowany może GET
                .antMatchers(HttpMethod.GET,    "/api/patients/**").hasAnyRole("USER", "ADMIN")
                .antMatchers(HttpMethod.POST,   "/api/patients/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.PUT,    "/api/patients/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/api/patients/**").hasRole("ADMIN")

                // 6. Wizyty: każdy zalogowany USER lub ADMIN może CRUD
                .antMatchers(HttpMethod.GET,    "/api/appointments/**").hasAnyRole("USER", "ADMIN")
                .antMatchers(HttpMethod.POST,   "/api/appointments/**").hasAnyRole("USER", "ADMIN")
                .antMatchers(HttpMethod.PUT,    "/api/appointments/**").hasAnyRole("USER", "ADMIN")
                .antMatchers(HttpMethod.DELETE, "/api/appointments/**").hasAnyRole("USER", "ADMIN")

                // 7. Wszystkie inne żądania wymagają uwierzytelnienia
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
