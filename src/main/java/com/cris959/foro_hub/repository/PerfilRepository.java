package com.cris959.foro_hub.repository;

import com.cris959.foro_hub.model.Perfil;
import com.cris959.foro_hub.model.PerfilNombre;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PerfilRepository extends JpaRepository<Perfil, Long> {

    /**
     * Busca un perfil específico por su nombre (Enum).
     * Retorna un Optional para gestionar de forma segura la posibilidad de que el perfil no exista.
     */
    Optional<Perfil> findByNombre(PerfilNombre nombre);

    /**
     * Valida si ya existe un perfil registrado con el nombre proporcionado.
     * Utilizado para controles de seguridad y evitar duplicidad de roles en el sistema.
     */
    boolean existsByNombre(@NotNull PerfilNombre nombre);
}
