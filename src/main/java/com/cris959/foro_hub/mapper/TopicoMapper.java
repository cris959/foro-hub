package com.cris959.foro_hub.mapper;

import com.cris959.foro_hub.dto.DatosRespuestaTopico;
import com.cris959.foro_hub.model.Topico;
import org.springframework.stereotype.Component;

@Component
public class TopicoMapper {

    public DatosRespuestaTopico toResponseDTO(Topico topico) {
        return new DatosRespuestaTopico(
                topico.getId(),
                topico.getTitulo(),
                topico.getMensaje(),
                topico.getFechaCreacion(),
                topico.getStatusTopico(),
                topico.getAutor().getNombre(),
                topico.getCurso().getNombre()
        );
    }
}
