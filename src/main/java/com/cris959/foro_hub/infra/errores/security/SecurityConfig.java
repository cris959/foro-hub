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
//       return http
//                .csrf(AbstractHttpConfigurer::disable)
//                // Es vital establecer la política STATELESS para JWT
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .authorizeHttpRequests(auth -> auth
//                        // 1. Recursos públicos (Swagger)
//                        .requestMatchers(
//                                "/v3/api-docs/**",
//                                "/swagger-ui/**",
//                                "/swagger-ui.html",
//                                "/webjars/**"
//                        ).permitAll()
//
//                        // 2. Login público
//                        .requestMatchers(HttpMethod.POST, "/login").permitAll()
//
//                        // 3. Reglas de Negocio (ADMIN)
//                        .requestMatchers(HttpMethod.POST, "/api/usuarios").hasRole("ADMIN")
//                        .requestMatchers(HttpMethod.PUT, "/api/usuarios").hasRole("ADMIN")
//                        .requestMatchers(HttpMethod.DELETE, "/api/usuarios/{id}").hasRole("ADMIN")
//
//                        // 4. Reglas Generales (USER o ADMIN)
//                        .requestMatchers("/api/**").hasAnyRole("USER", "ADMIN")
//
//                        // 5. Todo lo demás requiere autenticación
//                        .anyRequest().authenticated()
//                )
//                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
//                .build(); // Retornamos la cadena de filtros construida
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 1. PÚBLICO: Documentación y Login
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/webjars/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/login").permitAll()

                        // 2. SOLO ADMIN: Gestión de infraestructura (Usuarios, Perfiles, Cursos)
                        .requestMatchers("/api/usuarios/**").hasRole("ADMIN")
                        .requestMatchers("/api/perfiles/**").hasRole("ADMIN")
                        .requestMatchers("/api/cursos/**").hasRole("ADMIN")

                        // 3. TÓPICOS Y RESPUESTAS:
                        // El ADMIN puede borrar o editar cualquier cosa
                        .requestMatchers(HttpMethod.DELETE, "/api/topicos/**", "/api/respuestas/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/topicos/**", "/api/respuestas/**").hasRole("ADMIN")

                        // El USER (y el ADMIN) pueden Crear y Leer
                        .requestMatchers(HttpMethod.POST, "/api/topicos", "/api/respuestas").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/topicos/**", "/api/respuestas/**").hasAnyRole("USER", "ADMIN")

                        // 4. BLOQUEO TOTAL: Cualquier otra ruta no definida requiere estar logueado
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
