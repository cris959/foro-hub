package com.cris959.foro_hub.infra.errores.security;

import com.cris959.foro_hub.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    // 1. Inyectar el valor desde la variable de entorno usando @Value
    @Value("${jwt.secret}")
    private String secret;
    private Key signingKey;

    @PostConstruct
    public void init() {
        //  Si secret es nulo o muy corto, esto fallará
        if (secret == null || secret.length() < 32) {
            throw new IllegalArgumentException("La propiedad jwt.secret debe tener al menos 32 caracteres.");
        }
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }


    // Corregido: Ahora recibe el objeto Usuario directamente
    public String generateToken(Usuario usuario) {
        // Extraemos el nombre del rol desde las authorities que definimos en Usuario
        List<String> roles = usuario.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        Map<String, Object> claims = Map.of("roles", roles);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(usuario.getEmail()) // Usamos el email como subject
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 2)) // 2 horas es más seguro
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false; // Token expirado o alterado
        }
    }

    public String getSubject(String token) {
        if (token == null) return null;

        try {
            return Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    // Nota: El procedimiento getRolesFromToken ya no es estrictamente necesario
    // si en el SecurityFilter vuelves a cargar al usuario de la DB,
    // pero lo dejamos por si quieres optimizar luego.
}
