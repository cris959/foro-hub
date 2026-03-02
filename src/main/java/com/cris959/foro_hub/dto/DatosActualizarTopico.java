package com.cris959.foro_hub.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Datos para actualizar título, mensaje o estado de un tópico")
public record DatosActualizarTopico(

//        @Schema(description = "ID del tópico a modificar", example = "1")
//        @NotNull(message = "El ID es obligatorio para actualizar")
//        Long id,

        @Schema(description = "Nuevo título para el tópico", example = "Duda sobre Spring Boot 3", maxLength = 100)
        @NotBlank(message = "Título no puede estar vacío")
        @Size(max = 100, message = "Título máximo 100 caracteres")
        String titulo,

        @Schema(description = "Contenido actualizado del mensaje", example = "Mi duda es sobre el manejo de excepciones...")
        @NotBlank(message = "Mensaje no puede estar vacío")
        @Size(max = 500, message = "Mensaje máximo 500 caracteres")
        String mensaje,

        @Schema(description = "Estado de visibilidad del tópico", example = "true")
        @NotNull(message = "Estado activo es obligatorio")
        Boolean activo
) {
}
//PUT /api/topicos/5
//Body: {
//        "titulo": "Nuevo título",
//        "mensaje": "Nuevo mensaje",
//        "activo": true
//        }
