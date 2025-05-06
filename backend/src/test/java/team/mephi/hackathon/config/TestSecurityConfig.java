package team.mephi.hackathon.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;
import team.mephi.hackathon.exceptions.GlobalExceptionHandler;
import team.mephi.hackathon.exceptions.EntityNotFoundException;

import java.util.HashMap;
import java.util.Map;

@TestConfiguration
@EnableWebFluxSecurity
public class TestSecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(org.springframework.security.config.web.server.ServerHttpSecurity http) {
        return http.authorizeExchange(exchanges -> exchanges.anyExchange().permitAll())
                .csrf(csrf -> csrf.disable())
                .build();
    }

    @Bean
    public GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler();
    }

    @RestControllerAdvice
    static class TestExceptionHandler {

        @ExceptionHandler(EntityNotFoundException.class)
        @ResponseStatus(HttpStatus.NOT_FOUND)
        public Mono<Map<String, String>> handleTransactionNotFound(EntityNotFoundException ex) {
            Map<String, String> error = new HashMap<>();
            error.put("error", ex.getMessage());
            return Mono.just(error);
        }
    }
}
