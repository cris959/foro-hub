package com.cris959.foro_hub.infra.exception;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "DatosErrorIA",
        description = "Información de error generado por IA")
public record DatosErrorIA(
        @Schema(description = "Mensaje descriptivo del error de IA",
                example = "Timeout en modelo de lenguaje")
        String error) {
}
