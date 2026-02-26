package com.cris959.foro_hub.dto;

import jakarta.validation.constraints.NotNull;

public record DatosActualizarTopico(
        @NotNull(message = "El ID es obligatorio para actualizar")
        Long id,
        String titulo,
        String mensaje,
        Boolean activo
) {
}
