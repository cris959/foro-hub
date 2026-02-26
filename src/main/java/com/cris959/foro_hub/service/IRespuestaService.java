package com.cris959.foro_hub.service;

import com.cris959.foro_hub.dto.DatosRegistroRespuesta;
import com.cris959.foro_hub.dto.DatosRetornoRespuesta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface IRespuestaService {

    DatosRetornoRespuesta registrar(DatosRegistroRespuesta datos);

    Page<DatosRetornoRespuesta> listarPorTopico(Long topicoId, Pageable paginacion);

    void marcarComoSolucion(Long id);
}
