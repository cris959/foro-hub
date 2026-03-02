package com.cris959.foro_hub.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Datos para actualizar usuario (PATCH/PUT)")
public record DatosActualizarUsuario(
//        @NotNull(message = "El ID es obligatorio para actualizar")
//        Long id,
        @Schema(description = "Nuevo nombre del usuario", example = "Juan Pérez")
        @Size(min = 2, max = 50, message = "Nombre entre 2-50 caracteres")
        String nombre,

        @Schema(description = "Nueva contraseña (opcional)", example = "nuevaPass123")
        @Size(min = 6, message = "Mínimo 6 caracteres")
        String password,

        @Schema(description = "Estado activo/inactivo", example = "true")
        @NotNull(message = "Estado activo requerido")
        Boolean activo,

        @Schema(description = "ID del perfil", example = "1")
        @NotNull(message = "Perfil requerido")
        Long perfilId
) {
}
