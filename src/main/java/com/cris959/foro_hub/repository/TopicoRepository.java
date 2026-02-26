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

    Page<Topico> findByActivoTrue(Pageable paginacion);

    boolean existsByTituloAndMensaje(String titulo, String mensaje);

    List<Topico> findAll();

    @Query(value = "SELECT * FROM topicos WHERE id = :id", nativeQuery = true)
    Optional<Topico> encontrarEliminadoPorId(Long id);
}
