package org.springframework.samples.petclinic.genai;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Secures the AI chatclient endpoint so that only authenticated users can trigger
 * LLM tool-calls (including data-mutating operations such as adding owners or pets).
 * Uses the reactive (WebFlux) security stack to match spring.main.web-application-type=reactive.
 * Spring Boot auto-generates a random password at startup (printed to the application
 * log) when no ReactiveUserDetailsService bean is present, providing a fail-closed default.
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
            .authorizeExchange(exchanges -> exchanges
                // Allow actuator and health endpoints without authentication
                .pathMatchers("/actuator/**").permitAll()
                // All other requests (including /chatclient) require authentication
                .anyExchange().authenticated()
            )
            .httpBasic(org.springframework.security.config.Customizer.withDefaults())
            .build();
    }

}
