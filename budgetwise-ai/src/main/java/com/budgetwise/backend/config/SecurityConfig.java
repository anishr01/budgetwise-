package com.budgetwise.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // ❌ Disable CSRF (JavaFX has no cookies)
                .csrf(csrf -> csrf.disable())

                // ❌ Disable login page / form
                .formLogin(form -> form.disable())

                // ❌ Disable HTTP Basic popup
                .httpBasic(basic -> basic.disable())

                // ✅ Allow ALL API requests (important)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/**").permitAll()
                        .anyRequest().permitAll()
                );

        return http.build();
    }
}

