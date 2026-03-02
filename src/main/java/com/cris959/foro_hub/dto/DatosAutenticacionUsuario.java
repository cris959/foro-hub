package com.cris959.foro_hub.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos para autenticación de usuario")
public record DatosAutenticacionUsuario(

        @Schema(description = "Email del usuario",
                example = "usuario@ejemplo.com",
                required = true)
        String email,

        @Schema(description = "Contraseña del usuario",
                example = "pass123",
                required = true)
        String password
) {
}
