package com.cris959.foro_hub.service;

import com.cris959.foro_hub.dto.DatosRegistroCurso;
import com.cris959.foro_hub.dto.DatosRespuestaCurso;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ICursoService {

    DatosRespuestaCurso registrar(DatosRegistroCurso datos);
    Page<DatosRespuestaCurso> listar(Pageable paginacion);
    DatosRespuestaCurso buscarPorId(Long id);
    void eliminar(Long id);
}
