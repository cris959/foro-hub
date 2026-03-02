package com.cris959.foro_hub.dto;

import com.cris959.foro_hub.model.Respuesta;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Datos detallados de respuesta para listado")
public record DatosRetornoRespuesta(

        @Schema(description = "ID único de la respuesta", example = "1")
        Long id,

        @Schema(description = "Contenido de la respuesta",
                example = "Para JWT usa esta configuración...")
        String mensaje,

        @Schema(description = "Fecha y hora de creación")
        LocalDateTime fechaCreacion,

        @Schema(description = "Nombre del autor", example = "Juan Doyle")
        String nombreAutor,

        @Schema(description = "Título del tópico", example = "Duda JWT")
        String tituloTopico,

        @Schema(description = "¿Respuesta marca como solución?", example = "false")
        Boolean solucion
) {
    // Agrega este constructor personalizado dentro del record
    public DatosRetornoRespuesta(Respuesta respuesta) {
        this(
                respuesta.getId(),
                respuesta.getMensaje(),
                respuesta.getFechaCreacion(),
                respuesta.getAutor().getNombre(),
                respuesta.getTopico().getTitulo(),
                respuesta.getSolucion()
        );
    }
}
