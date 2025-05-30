package team.mephi.hackathon.security;

import static org.springframework.security.authorization.AuthorityReactiveAuthorizationManager.hasAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;

/**
 * Конфигурационный класс для настройки безопасности приложения. Настраивает: - OAuth2 Resource
 * Server (JWT) - CORS - Авторизацию через роли
 */
@Configuration
public class SecurityConfiguration {
  /**
   * URL-адрес, по которому доступны публичные ключи для проверки JWT токенов. Берётся из
   * application.properties.
   */
  @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
  String jwkSetUri;

  /** Список разрешённых источников (origins) для CORS. Берётся из application.properties. */
  @Value("${app.http.cors.origins}")
  List<String> httpCorsOrigins;

  /** Список разрешённых заголовков для CORS. Берётся из application.properties. */
  @Value("${app.http.cors.headers}")
  List<String> httpCorsHeaders;

  /** Список разрешённых HTTP-методов для CORS. Берётся из application.properties. */
  @Value("${app.http.cors.methods}")
  List<String> httpCorsMethods;

  /** Роль, которая требуется для доступа к API. Берётся из application.properties. */
  @Value("${app.role.users}")
  String userRole;

  /** Включена ли аутентификация. Берётся из application.properties. */
  @Value("${app.auth.enabled}")
  boolean authEnabled;

  /**
   * Настраивает цепочку фильтров безопасности. Если аутентификация включена — защищает /api/** по
   * ролям. Иначе — разрешает всё.
   *
   * @param http конфигурация HttpSecurity
   * @return настроенная цепочка фильтров
   */
  @Bean
  SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
    if (authEnabled) {
      http.cors(Customizer.withDefaults())
          .authorizeExchange(
              (authorize) ->
                  authorize
                      .pathMatchers("/api/**")
                      .access(hasAuthority(userRole))
                      .anyExchange()
                      .permitAll())
          .oauth2ResourceServer(
              resourceServer ->
                  resourceServer.jwt(
                      jwt -> jwt.jwtAuthenticationConverter(grantedAuthoritiesExtractor())));
    } else {
      http.csrf(ServerHttpSecurity.CsrfSpec::disable);
    }
    return http.build();
  }

  Converter<Jwt, Mono<AbstractAuthenticationToken>> grantedAuthoritiesExtractor() {
    JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new GrantedAuthoritiesExtractor());
    return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
  }

  /**
   * Создаёт и настраивает декодер JWT для проверки токенов.
   *
   * @return реактивный декодер JWT
   */
  @Bean
  ReactiveJwtDecoder reactiveJwtDecoder() {
    return NimbusReactiveJwtDecoder.withJwkSetUri(jwkSetUri).build();
  }

  /**
   * Настройка CORS: разрешённые источники, методы, заголовки.
   *
   * @return источник конфигурации CORS
   */
  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(httpCorsOrigins);
    configuration.setAllowedHeaders(httpCorsHeaders);
    configuration.setAllowedMethods(httpCorsMethods);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  static class GrantedAuthoritiesExtractor implements Converter<Jwt, Collection<GrantedAuthority>> {
    public Collection<GrantedAuthority> convert(Jwt jwt) {
      Map<?, List<?>> authorities =
          (Map<?, List<?>>) jwt.getClaims().getOrDefault("realm_access", Collections.emptyMap());

      List<?> roles = authorities.getOrDefault("roles", Collections.emptyList());

      return roles.stream()
          .map(Object::toString)
          .map(SimpleGrantedAuthority::new)
          .collect(Collectors.toList());
    }
  }
}
