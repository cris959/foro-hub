package com.cris959.foro_hub.repository;

import com.cris959.foro_hub.model.Curso;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {

    Page<Curso> findByActivoTrue(Pageable paginacion);

    // Si prefieres la validación doble (recomendada):
    boolean existsByNombreAndCategoria(String nombre, String categoria);
}
