package com.cris959.foro_hub.dto;

import java.time.LocalDateTime;

public record DatosRetornoRespuesta(
        Long id,
        String mensaje,
        LocalDateTime fechaCreacion,
        String nombreAutor,
        String tituloTopico
) {
}
