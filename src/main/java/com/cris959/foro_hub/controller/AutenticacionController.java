package com.cris959.foro_hub.controller;

import com.cris959.foro_hub.dto.DatosAutenticacionUsuario;
import com.cris959.foro_hub.dto.DatosJWTToken;
import com.cris959.foro_hub.infra.errores.security.JwtTokenProvider;
import com.cris959.foro_hub.model.Usuario;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class AutenticacionController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping
    public ResponseEntity autenticar(@RequestBody @Valid DatosAutenticacionUsuario datos) {
        // Esta llamada dispara el proceso de Spring Security
        var authenticationToken = new UsernamePasswordAuthenticationToken(datos.email(), datos.password());

        // Aquí es donde ocurre el StackOverflow si hay dependencias circulares
        var usuarioAutenticado = authenticationManager.authenticate(authenticationToken);

        var JWTtoken = jwtTokenProvider.generateToken((Usuario) usuarioAutenticado.getPrincipal());

        return ResponseEntity.ok(new DatosJWTToken(JWTtoken));
    }
}
