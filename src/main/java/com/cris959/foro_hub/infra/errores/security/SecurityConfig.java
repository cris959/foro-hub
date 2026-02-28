package com.cris959.foro_hub.infra.errores.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    @Lazy // Importante para romper el ciclo
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 1. PÚBLICO
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/webjars/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/login").permitAll()

                        // 2. LECTURA (Permitir a USER y ADMIN consultar antes de bloquear el resto)
                        // Ponemos esto ARRIBA para que el GET no sea bloqueado por la regla de ADMIN
                        .requestMatchers(HttpMethod.GET, "/api/cursos/**", "/api/topicos/**", "/api/respuestas/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/perfiles/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/usuarios/{id}").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/usuarios/buscar").hasAnyRole("USER", "ADMIN")

                        // 3. CREACIÓN (USER y ADMIN)
                        .requestMatchers(HttpMethod.POST, "/api/topicos/**", "/api/respuestas/**").hasAnyRole("USER", "ADMIN")

                        // 4. SOLO ADMIN (Gestión y Moderación)
                        // Ahora sí, cualquier otra operación (POST, PUT, DELETE) en estas rutas es solo ADMIN
                        .requestMatchers("/api/usuarios/**").hasRole("ADMIN")
                        .requestMatchers("/api/perfiles/**").hasRole("ADMIN")
                        .requestMatchers("/api/cursos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/topicos/**", "/api/respuestas/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/topicos/**", "/api/respuestas/**").hasRole("ADMIN")
                        .requestMatchers("/api/respuestas/*/solucion").hasRole("ADMIN")

                        // 5. CIERRE
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
