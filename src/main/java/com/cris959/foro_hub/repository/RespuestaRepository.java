package com.cris959.foro_hub.repository;

import com.cris959.foro_hub.model.Respuesta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface RespuestaRepository extends JpaRepository<Respuesta, Long> {

    // Busca una respuesta por ID de tópico
    Page<Respuesta> findByTopicoId(Long idTopico, Pageable paginacion);

    // Busca todas las respuestas asociadas a un ID de tópico
    Page<Respuesta> findAllByTopicoId(Long topicoId, Pageable paginacion);

    @Modifying
    @Query("UPDATE Respuesta r SET r.solucion = false WHERE r.topico.id = :topicoId")
    void desmarcarOtrasSoluciones(Long topicoId);
}
