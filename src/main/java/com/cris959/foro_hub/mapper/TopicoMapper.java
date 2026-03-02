package com.cris959.foro_hub.mapper;

import com.cris959.foro_hub.dto.DatosRespuestaTopico;
import com.cris959.foro_hub.model.Curso;
import com.cris959.foro_hub.model.Topico;
import com.cris959.foro_hub.model.Usuario;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TopicoMapper {

    public DatosRespuestaTopico toResponseDTO(Topico topico) {
        // Validamos que el tópico no sea nulo
        if (topico == null) return null;

        if (topico == null) return null;

        return new DatosRespuestaTopico(
                topico.getId(),
                topico.getTitulo(),
                topico.getMensaje(),
                topico.getFechaCreacion(),
                topico.getStatusTopico(),
                Optional.ofNullable(topico.getAutor())
                        .map(Usuario::getNombre)
                        .orElse("Autor anónimo"),
                Optional.ofNullable(topico.getCurso())
                        .map(Curso::getNombre)
                        .orElse("Sin curso"),
                Optional.ofNullable(topico.getAutor())
                        .map(Usuario::getActivo)
                        .orElse(false),
                topico.getActivo()
        );
    }
}
