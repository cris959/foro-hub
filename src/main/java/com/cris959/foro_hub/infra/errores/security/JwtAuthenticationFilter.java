package com.cris959.foro_hub.infra.errores.security;

import com.cris959.foro_hub.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = getJwtFromRequest(request);
// 2. Validar el token y extraer el "subject" (email)
        if (token != null && jwtTokenProvider.validateToken(token)) {
            String email = jwtTokenProvider.getSubject(token); // Cambiado getUsernameFromJWT por getSubject

            // 3. Buscar al usuario en la DB para confirmar que sigue activo
            var usuarioOptional = usuarioRepository.findByEmail(email);

            if (usuarioOptional.isPresent()) {
                var usuario = usuarioOptional.get();

                // 4. Crear la autenticación con el objeto Usuario completo y sus roles reales
                var authentication = new UsernamePasswordAuthenticationToken(
                        usuario,
                        null,
                        usuario.getAuthorities()
                );

                // 5. Setear la autenticación en el contexto de Spring
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // 6. Continuar con el siguiente filtro en la cadena
        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.replace("Bearer ", "");
        }
        return null;
    }
}
