package com.cris959.foro_hub.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Respuesta completa del análisis de tendencias")
public record AnalisisTendenciasResponse(
        @Schema(description = "Top 3 temas más recurrentes", example = "[\"Java\", \"Spring Boot\", \"JPA\"]")
        List<String> tendencias,

        @Schema(description = "Sugerencia de contenido basada en tendencias",
                example = "Crear curso de Spring Boot con JPA avanzado")
        String sugerencia,

        @Schema(description = "Fuente del análisis", example = "IA_ANALISIS")
        String fuente,

        @Schema(description = "Fecha del análisis", example = "04/03/2026 21:44")
        String fecha_analisis
) {
}
