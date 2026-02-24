package com.cris959.foro_hub.dto;

import jakarta.validation.constraints.NotNull;

public record DatosActualizarUsuario(
        @NotNull(message = "El ID es obligatorio para actualizar")
        Long id,
        String nombre,
        String password,
        Boolean activo,
        Long perfilId
) {
}
