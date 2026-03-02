package com.cris959.foro_hub.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta completa de usuario con perfil")
public record DatosRespuestaUsuario(

        @Schema(example = "1")
        Long id,

        @Schema(example = "Juan Doyle")
        String nombre,

        @Schema(example = "juan@email.com")
        String email,

        @Schema(description = "Perfil del usuario (puede ser null)")
        DatosListaPerfil perfil // Usamos el DTO de perfil que creamos antes
) {
}
