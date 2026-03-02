package com.cris959.foro_hub.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Datos necesarios para crear un nuevo tópico en el foro")
public record DatosRegistroTopico(

        @Schema(description = "Título descriptivo del tópico", example = "Error 403 en Spring Security")
        @NotBlank(message = "El título es obligatorio y debe ser único")
        @Size(min = 5, max = 100, message = "El título debe tener entre 5 y 100 caracteres")
        String titulo,

        @Schema(description = "Contenido detallado de la consulta", example = "Tengo un problema al configurar los requestMatchers...")
        @NotBlank@NotBlank(message = "El mensaje no puede estar vacío")
        @Size(min = 10, max = 500, message = "El mensaje debe tener entre 10 y 500 caracteres")
        String mensaje,

        @Schema(description = "ID del usuario que crea el tópico", example = "3")
        @NotNull@NotNull(message = "El ID del autor es obligatorio")
        Long idAutor,

        @Schema(description = "ID del curso relacionado al tópico", example = "1")
        @NotNull(message = "El ID del curso es obligatorio")
        Long idCurso
) {
}
