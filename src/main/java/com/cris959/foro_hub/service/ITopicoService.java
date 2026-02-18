package com.cris959.foro_hub.service;

import com.cris959.foro_hub.dto.DatosRegistroTopico;
import com.cris959.foro_hub.dto.DatosRespuestaTopico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ITopicoService {

    DatosRespuestaTopico registrar(DatosRegistroTopico datos);

    Page<DatosRespuestaTopico> listar(Pageable paginacion);

    DatosRespuestaTopico buscarPorId(Long id);

    DatosRespuestaTopico actualizar(Long id, DatosRespuestaTopico datos);

    void eliminar(Long id);
}
