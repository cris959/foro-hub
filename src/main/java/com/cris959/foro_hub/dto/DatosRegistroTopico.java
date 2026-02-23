package com.cris959.foro_hub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DatosRegistroTopico(
        @NotBlank(message = "El título es obligatorio y debe ser único")
        String titulo,
        @NotBlank@NotBlank(message = "El mensaje no puede estar vacío")
        @Size(min = 10, max = 500, message = "El mensaje debe tener entre 10 y 500 caracteres")
        String mensaje,
        @NotNull@NotNull(message = "El ID del autor es obligatorio")
        Long idAutor,
        @NotNull(message = "El ID del curso es obligatorio")
        Long idCurso
) {
}
