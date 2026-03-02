package com.cris959.foro_hub.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos de curso para respuestas de lectura")
public record DatosRespuestaCurso(

        @Schema(description = "ID único del curso",
                example = "1")
        Long id,

        @Schema(description = "Nombre del curso",
                example = "Spring Boot Avanzado")
        String nombre,

        @Schema(description = "Categoría del curso",
                example = "Backend")
        String categoria
) {
}
