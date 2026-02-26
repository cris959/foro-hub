package com.cris959.foro_hub.repository;

import com.cris959.foro_hub.model.Respuesta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Repository
public interface RespuestaRepository extends JpaRepository<Respuesta, Long> {

    // Busca todas las respuestas asociadas a un ID de tópico
    Page<Respuesta> findAllByTopicoId(Long topicoId, Pageable paginacion);

    /**
     * Resetea todas las soluciones de un tópico específico.
     * El atributo clearAutomatically asegura que la caché de JPA se actualice
     * para evitar inconsistencias de datos en memoria tras la modificación.
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE Respuesta r SET r.solucion = false WHERE r.topico.id = :topicoId")
    void desmarcarOtrasSoluciones(Long topicoId);

    // forzar la carga para evitar LazyInitializationException
    @Query("SELECT r FROM Respuesta r JOIN FETCH r.topico WHERE r.id = :id")
    Optional<Respuesta> findByIdWithTopico(Long id);

    // Para evitar el duplicado
    boolean existsByTopicoIdAndMensaje(Long topicoId, String mensaje);

    // Para traer ABSOLUTAMENTE TODAS las respuestas de la DB (Paginadas)
    @Query("SELECT r FROM Respuesta r")
    Page<Respuesta> findAll(Pageable paginacion);

    // Para traer todas las respuestas de un usuario específico
    Page<Respuesta> findAllByAutorId(Long autorId, Pageable paginacion);
}
