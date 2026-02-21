package com.cris959.foro_hub.repository;

import com.cris959.foro_hub.model.Respuesta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RespuestaRepository extends JpaRepository<Respuesta, Long> {

    // Busca una respuesta por ID de tópico
    Page<Respuesta> findByTopicoId(Long idTopico, Pageable paginacion);

    // Busca todas las respuestas asociadas a un ID de tópico
    List<Respuesta> findAllByTopicoId(Long topicoId);
}
