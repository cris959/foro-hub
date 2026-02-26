package com.cris959.foro_hub.dto;

import com.cris959.foro_hub.model.Respuesta;

import java.time.LocalDateTime;

public record DatosRetornoRespuesta(
        Long id,
        String mensaje,
        LocalDateTime fechaCreacion,
        String nombreAutor,
        String tituloTopico,
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
