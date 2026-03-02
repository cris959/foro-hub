package com.cris959.foro_hub.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Datos para registrar un nuevo curso")
public record DatosRegistroCurso(

        @Schema(description = "Nombre del curso",
                example = "Spring Boot Avanzado",
                required = true)
        @NotBlank String nombre,

        @Schema(description = "Categoría del curso",
                example = "Backend",
                required = true)
        @NotBlank String categoria
) {
}
