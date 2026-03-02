package com.cris959.foro_hub.controller;

import com.cris959.foro_hub.dto.DatosAutenticacionUsuario;
import com.cris959.foro_hub.dto.DatosJWTToken;
import com.cris959.foro_hub.infra.security.JwtTokenProvider;
import com.cris959.foro_hub.model.Usuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@Tag(
        name = "Autenticación",
        description = "Endpoint público de login. Genera JWT token para acceder a APIs protegidas."
)
@RestController
@RequestMapping("/login")
public class AutenticacionController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Operation(
            summary = "Iniciar sesión",
            description = "Autentica usuario y retorna JWT Bearer token para APIs protegidas. **PÚBLICO** (sin token requerido)."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Login exitoso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DatosJWTToken.class))
            ),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas"),
            @ApiResponse(responseCode = "400", description = "Datos de login inválidos")
    })
    @PostMapping
    public ResponseEntity<DatosJWTToken> autenticar(@RequestBody @Valid DatosAutenticacionUsuario datos) {
       try {
           // Esta llamada dispara el proceso de Spring Security
           var authenticationToken = new UsernamePasswordAuthenticationToken(datos.email(), datos.password());

           // Aquí es donde ocurre el StackOverflow si hay dependencias circulares
           var usuarioAutenticado = authenticationManager.authenticate(authenticationToken);

           var JWTtoken = jwtTokenProvider.generateToken((Usuario) usuarioAutenticado.getPrincipal());

           return ResponseEntity.ok(new DatosJWTToken(JWTtoken));
       } catch (BadCredentialsException e) {
           throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas!");
       }
    }
}
