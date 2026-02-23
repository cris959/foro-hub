package com.cris959.foro_hub.repository;

import com.cris959.foro_hub.model.Perfil;
import com.cris959.foro_hub.model.PerfilNombre;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PerfilRepository extends JpaRepository<Perfil, Long> {

    Optional<Perfil> findByNombre(PerfilNombre nombre);

    boolean existsByNombre(@NotNull PerfilNombre nombre);
}
