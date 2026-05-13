package com.helppet.config;

import com.helppet.filter.RequestContextFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuracao de seguranca do microservico HelpPet.
 *
 * Este servico confia nos headers injetados pelo Gateway (X-User-Id, X-User-Email).
 * O endpoint /api/v1/users/login e publico pois e o ponto de autenticacao.
 * Todos os outros endpoints exigem os headers do Gateway.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final RequestContextFilter requestContextFilter;

    public SecurityConfig(RequestContextFilter requestContextFilter) {
        this.requestContextFilter = requestContextFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Login e criacao de conta sao publicos
                        .requestMatchers(HttpMethod.POST, "/api/v1/users/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/users").permitAll()
                        // Actuator health e publico para o Gateway verificar saude
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/api/health").permitAll()
                        .requestMatchers("/error").permitAll()
                        // Todo o resto exige headers do Gateway
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write(
                                    "{\"success\":false,\"message\":\"Nao autorizado: acesse via API Gateway\"}"
                            );
                        })
                )
                .addFilterBefore(requestContextFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Bean do BCryptPasswordEncoder.
     * Usado em UserService para hashear senhas e em UserController para validar.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
