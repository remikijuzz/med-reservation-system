package org.example.medreservationsystem.config;

import org.example.medreservationsystem.security.JwtAuthenticationFilter;
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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          CustomUserDetailsService userDetailsService) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authBuilder =
            http.getSharedObject(AuthenticationManagerBuilder.class);
        authBuilder
            .userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder());
        return authBuilder.build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors().and()
            .csrf().disable()
            .headers().frameOptions().disable().and()
            .sessionManagement()
               .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
               // publiczne
               .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
               .antMatchers(
                   "/api/auth/**",
                   "/swagger-ui.html", "/swagger-ui/**",
                   "/v3/api-docs/**", "/swagger-resources/**",
                   "/webjars/**"
               ).permitAll()

               // ADMIN only: CRUD lekarzy & pacjentów
               .antMatchers(HttpMethod.POST,   "/api/doctors/**", "/api/patients/**").hasRole("ADMIN")
               .antMatchers(HttpMethod.PUT,    "/api/doctors/**", "/api/patients/**").hasRole("ADMIN")
               .antMatchers(HttpMethod.DELETE, "/api/doctors/**", "/api/patients/**").hasRole("ADMIN")

               // wszyscy zalogowani mogą GET doctors & patients
               .antMatchers(HttpMethod.GET, "/api/doctors/**", "/api/patients/**")
                  .hasAnyRole("USER","ADMIN","DOCTOR","PATIENT")

               // Appointment: listowanie własne pacjenta
               .antMatchers(HttpMethod.GET, "/api/appointments/patient/**")
                  .hasAnyRole("PATIENT","DOCTOR","ADMIN")
               // Appointment: listowanie przez lekarza/admina
               .antMatchers(HttpMethod.GET, "/api/appointments/doctor/**")
                  .hasAnyRole("DOCTOR","ADMIN")
               // Appointment: pełna lista tylko dla doctor/admin
               .antMatchers(HttpMethod.GET, "/api/appointments").hasAnyRole("DOCTOR","ADMIN")
               // pojedyncza wizyta — PATIENT (własna), DOCTOR, ADMIN
               .antMatchers(HttpMethod.GET, "/api/appointments/*")
                  .hasAnyRole("PATIENT","DOCTOR","ADMIN")

               // Appointment: tworzenie tylko PACIENT
               .antMatchers(HttpMethod.POST, "/api/appointments").hasRole("PATIENT")
               // aktualizacja tylko PACIENT
               .antMatchers(HttpMethod.PUT,    "/api/appointments/*").hasRole("PATIENT")
               // usuwanie dla PACIENT i ADMIN
               .antMatchers(HttpMethod.DELETE, "/api/appointments/*")
                  .hasAnyRole("PATIENT","ADMIN")

               .anyRequest().authenticated()
            .and()
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("*"));
        config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization","Content-Type"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
