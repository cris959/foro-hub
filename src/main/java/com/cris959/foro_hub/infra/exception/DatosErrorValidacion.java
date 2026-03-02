package com.cris959.foro_hub.infra.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.validation.FieldError;

@Schema(description = "Error de validación de un campo específico")
public record DatosErrorValidacion(

        @Schema(description = "Nombre del campo con error",
                example = "mensaje")
        String campo,

        @Schema(description = "Mensaje de error específico",
                example = "El mensaje no puede estar vacío")
        String error
) {
    // Necesitas este constructor para que funcione tu código
    public DatosErrorValidacion(FieldError error) {
        this(error.getField(), error.getDefaultMessage());
    }
}
