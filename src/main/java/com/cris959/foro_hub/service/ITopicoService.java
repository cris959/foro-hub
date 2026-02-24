package com.cris959.foro_hub.service;

import com.cris959.foro_hub.dto.DatosActualizarTopico;
import com.cris959.foro_hub.dto.DatosRegistroTopico;
import com.cris959.foro_hub.dto.DatosRespuestaTopico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ITopicoService {

    DatosRespuestaTopico crear(DatosRegistroTopico datos);

    Page<DatosRespuestaTopico> listar(Pageable paginacion);

    DatosRespuestaTopico buscarPorId(Long id);

    DatosRespuestaTopico actualizar(Long id, DatosActualizarTopico datos);

    void eliminar(Long id);

    // Procedimeinto para listar todos (activos e inactivos, ya que no usas @Where)
    List<DatosRespuestaTopico> listarTodoElHistorial();
}
