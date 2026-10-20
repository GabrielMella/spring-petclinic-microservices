package org.springframework.samples.petclinic.genai;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Secures the GenAI service endpoints.
 *
 * <p>The {@code /chatclient} endpoint requires HTTP Basic authentication so that
 * unauthenticated callers cannot trigger data-mutating AI tool calls via prompt injection.
 * Credentials are supplied through standard Spring Boot properties
 * ({@code spring.security.user.name} / {@code spring.security.user.password}).
 *
 * <p>Actuator health and info probes are permitted without credentials so that
 * service-discovery and load-balancer health checks continue to work.
 */
@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Allow infrastructure probes without credentials.
                .requestMatchers(
                    "/actuator/health",
                    "/actuator/health/**",
                    "/actuator/info"
                ).permitAll()
                // Every other request, including POST /chatclient, requires authentication.
                .anyRequest().authenticated()
            )
            // Stateless HTTP Basic auth — no session cookie is created.
            .httpBasic(basic -> {})
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            // CSRF protection is unnecessary for a stateless API.
            .csrf(csrf -> csrf.disable());
        return http.build();
    }
}
