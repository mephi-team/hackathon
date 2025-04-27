package team.mephi.hackathon.controller;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@TestConfiguration
public class TestSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception, java.io.IOException {
        return http.csrf(csrf -> csrf.disable()).authorizeHttpRequests(registry -> registry.anyRequest().permitAll()).build();
    }
}
