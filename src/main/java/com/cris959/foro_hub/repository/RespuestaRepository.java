package com.cris959.foro_hub.repository;

import com.cris959.foro_hub.model.Respuesta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RespuestaRepository extends JpaRepository<Respuesta, Long> {

    // Busca una respuesta por ID de tópico
    List<Respuesta> findByTopicoId(Long idTopico);

    // Busca todas las respuestas asociadas a un ID de tópico
    List<Respuesta> findAllByTopicoId(Long topicoId);
}
