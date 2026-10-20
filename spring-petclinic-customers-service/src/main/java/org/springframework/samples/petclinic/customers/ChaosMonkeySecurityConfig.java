package org.springframework.samples.petclinic.customers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Protects Chaos Monkey actuator endpoints with HTTP Basic authentication.
 * All other service endpoints remain publicly accessible.
 * Requires CHAOS_MONKEY_USER and CHAOS_MONKEY_PASSWORD environment variables;
 * the application will refuse to start if either is absent or blank.
 */
@Configuration
@EnableWebSecurity
public class ChaosMonkeySecurityConfig {

    @Bean
    public SecurityFilterChain chaosMonkeySecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/actuator/chaosmonkey/**")
            .authorizeHttpRequests(auth -> auth
                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable());
        return http.build();
    }

    @Bean
    public UserDetailsService chaosMonkeyUserDetailsService(
            @Value("${CHAOS_MONKEY_USER:}") String username,
            @Value("${CHAOS_MONKEY_PASSWORD:}") String password) {
        if (username == null || username.isBlank()) {
            throw new IllegalStateException(
                "CHAOS_MONKEY_USER environment variable must be set to a non-empty value");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalStateException(
                "CHAOS_MONKEY_PASSWORD environment variable must be set to a non-empty value");
        }
        UserDetails user = User.builder()
            .username(username)
            .password("{noop}" + password)
            .roles("ACTUATOR")
            .build();
        return new InMemoryUserDetailsManager(user);
    }
}
