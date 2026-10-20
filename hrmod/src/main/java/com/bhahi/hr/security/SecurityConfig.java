package com.bhahi.hr.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())   // fine for a stateless REST API
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/mailing-api", "/error").permitAll()
                        .anyRequest().authenticated()
                );
        return http.build();
    }
}