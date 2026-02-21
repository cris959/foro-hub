package com.cris959.foro_hub.mapper;

import com.cris959.foro_hub.dto.DatosRespuestaTopico;
import com.cris959.foro_hub.model.Topico;
import org.springframework.stereotype.Component;

@Component
public class TopicoMapper {

    public DatosRespuestaTopico toResponseDTO(Topico topico) {
        // Validamos que el tópico no sea nulo
        if (topico == null) return null;

        return new DatosRespuestaTopico(
                topico.getId(),
                topico.getTitulo(),
                topico.getMensaje(),
                topico.getFechaCreacion(),
                topico.getStatusTopico(),
                topico.getAutor() != null ? topico.getAutor().getNombre() : "Autor anónimo",
                topico.getCurso() != null ? topico.getCurso().getNombre() : "Curso no asignado"
        );
    }
}
