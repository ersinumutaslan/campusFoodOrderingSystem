package com.campusfood.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/** Who may call which endpoint. Login is email + password on every request (HTTP Basic). */
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Public: browsing, API docs, error responses
                        .requestMatchers(HttpMethod.GET, "/", "/hello", "/restaurants/**").permitAll()
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/error").permitAll()
                        // Only students place orders; only staff move orders through the kitchen
                        .requestMatchers(HttpMethod.POST, "/orders").hasRole("STUDENT")
                        .requestMatchers(HttpMethod.PATCH, "/orders/*/status").hasRole("STAFF")
                        // Everything else needs a login
                        .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults())
                // Each request carries its own login, so no server session and no CSRF token are needed
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(csrf -> csrf.disable());
        return http.build();
    }

    /** Checks passwords against stored hashes like "{bcrypt}$2a$10$...". */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
