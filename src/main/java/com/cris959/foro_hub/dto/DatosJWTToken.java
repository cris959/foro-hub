package com.cris959.foro_hub.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta con token JWT para autenticación")
public record DatosJWTToken(

        @Schema(description = "Token JWT generado para el usuario",
                example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                required = true)
        String jwtToken
) {
}
