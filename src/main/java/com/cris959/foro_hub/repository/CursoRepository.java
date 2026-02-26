package com.cris959.foro_hub.repository;

import com.cris959.foro_hub.model.Curso;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {

    /**
     * Recupera todos los cursos cuyo estado es activo (lógica de borrado suave).
     * Devuelve los resultados de forma paginada para optimizar el rendimiento.
     */
    Page<Curso> findByActivoTrue(Pageable paginacion);

    /**
     * Verifica la existencia de un curso por su nombre y categoría exactos.
     * Se utiliza para validar y evitar la creación de cursos duplicados.
     */
    boolean existsByNombreAndCategoria(String nombre, String categoria);
}
