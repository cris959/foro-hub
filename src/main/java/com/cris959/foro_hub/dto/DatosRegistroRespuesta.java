package com.cris959.foro_hub.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Datos para registrar una respuesta en un tópico")
public record DatosRegistroRespuesta(

        @Schema(description = "Mensaje/contenido de la respuesta",
                example = "Excelente explicación del tema Spring Boot",
                required = true)
        @NotBlank String mensaje,

        @Schema(description = "ID del autor/usuario que responde",
                example = "1",
                required = true)
        @NotNull Long autorId,

        @Schema(description = "ID del tópico donde se responde",
                example = "5",
                required = true)
        @NotNull Long topicoId
) {
}
