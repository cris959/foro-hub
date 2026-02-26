package com.cris959.foro_hub.repository;

import com.cris959.foro_hub.model.Topico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TopicoRepository extends JpaRepository<Topico, Long> {

    /**
     * Recupera el listado de tópicos activos de forma paginada.
     * Filtra automáticamente aquellos registros que han sido eliminados lógicamente.
     */
    Page<Topico> findByActivoTrue(Pageable paginacion);

    /**
     * Valida si existe un tópico con el mismo título y mensaje.
     * Se utiliza para prevenir que el usuario publique contenido duplicado accidentalmente.
     */
    boolean existsByTituloAndMensaje(String titulo, String mensaje);

    /**
     * Recupera la lista completa de tópicos en la base de datos sin paginación.
     * Útil para reportes o procesos internos que requieren la totalidad de los datos.
     */
    List<Topico> findAll();

    /**
     * Consulta nativa para recuperar un tópico por su ID, ignorando si está activo o no.
     * Permite acceder a registros que han sido marcados como eliminados para auditoría.
     */
    @Query(value = "SELECT * FROM topicos WHERE id = :id", nativeQuery = true)
    Optional<Topico> encontrarEliminadoPorId(Long id);
}
