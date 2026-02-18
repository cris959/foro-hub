package com.cris959.foro_hub.mapper;

import com.cris959.foro_hub.dto.DatosRespuestaCurso;
import com.cris959.foro_hub.model.Curso;
import org.springframework.stereotype.Component;

@Component
public class CursoMapper {

    public DatosRespuestaCurso toResponseDTO(Curso curso) {
        return new DatosRespuestaCurso(
                curso.getId(),
                curso.getNombre(),
                curso.getCategoria()
        );
    }
}
