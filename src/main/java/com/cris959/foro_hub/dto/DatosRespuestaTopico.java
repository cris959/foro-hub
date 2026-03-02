package com.cris959.foro_hub.dto;

import com.cris959.foro_hub.model.StatusTopico;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Datos detallados de tópico para respuestas de lectura")
public record DatosRespuestaTopico(

        @Schema(description = "ID único del tópico", example = "1")
        Long id,

        @Schema(description = "Título del tópico", example = "Duda sobre Spring Security")
        String titulo,

        @Schema(description = "Mensaje inicial del tópico", example = "¿Cómo configuro JWT?")
        String mensaje,

        @Schema(description = "Fecha y hora de creación")
        LocalDateTime fechaCreacion,

        @Schema(description = "Estado del tópico", example = "NO_RESPONDIDO")
        StatusTopico statusTopico,

        @Schema(description = "Nombre del autor", example = "Juan Doyle")
        String nombreAutor,

        @Schema(description = "Curso asociado", example = "Spring Boot Avanzado")
        String nombreCurso,

        @Schema(description = "Cuenta del autor activa", example = "true")
        Boolean autorActivo,  // Estado de la cuenta del usuario

        @Schema(description = "Tópico visible (no eliminado)", example = "true")
        Boolean topicoActivo  // Estado de visibilidad del post (borrado lógico)
) {
}
