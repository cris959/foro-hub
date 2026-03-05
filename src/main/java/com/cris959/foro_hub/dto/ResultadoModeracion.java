package com.cris959.foro_hub.dto;


import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "ResultadoModeracion",
        description = "Respuesta de moderación de contenido")
public record ResultadoModeracion(

        @Schema(description = "Fuente origen del análisis de moderación", example = "chatbot_usuario")
        String fuente,

        @Schema(description = "Detalle del resultado de moderación", example = "Contenido con lenguaje ofensivo")
        String detalle,

        @Schema(description = "Indica si el contenido fue bloqueado", example = "true")
        boolean bloqueado
) {
}
