package com.cris959.foro_hub.dto;

import com.cris959.foro_hub.model.StatusTopico;

import java.time.LocalDateTime;

public record DatosRespuestaTopico(
        Long id,
        String titulo,
        String mensaje,
        LocalDateTime fechaCreacion,
        StatusTopico statusTopico,
        String nombreAutor,
        String nombreCurso,
        Boolean autorActivo,  // Estado de la cuenta del usuario
        Boolean topicoActivo  // Estado de visibilidad del post (borrado lógico)
) {
}
