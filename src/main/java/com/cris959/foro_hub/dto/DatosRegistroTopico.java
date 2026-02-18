package com.cris959.foro_hub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DatosRegistroTopico(
        @NotBlank String titulo,
        @NotBlank String mensaje,
        @NotNull Long idAutor,
        @NotNull Long idCurso
) {
}
