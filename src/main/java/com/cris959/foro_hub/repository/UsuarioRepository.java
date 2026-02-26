package com.cris959.foro_hub.repository;

import com.cris959.foro_hub.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca un usuario activo por su correo electrónico.
     * Es el procedimiento principal para el proceso de autenticación y carga de detalles de usuario.
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Consulta nativa para contar registros con un correo específico.
     * Proporciona una validación rápida y de bajo costo para verificar si un email ya está registrado.
     */
    @Query(value = "SELECT COUNT(*) FROM usuarios WHERE email = :email", nativeQuery = true)
    Long countByEmailNative(String email);

    /**
     * Recupera un usuario por su ID mediante SQL nativo, ignorando filtros de estado.
     * Permite acceder a cuentas desactivadas o eliminadas para procesos de soporte o auditoría.
     */
    @Query(value = "SELECT * FROM usuarios WHERE id = :id", nativeQuery = true)
    Optional<Usuario> encontrarEliminadoPorId(Long id);

    /**
     * Obtiene el listado completo de usuarios con estado inactivo.
     * Útil para paneles de administración que gestionan usuarios baneados o cuentas suspendidas.
     */
    @Query(value = "SELECT * FROM usuarios WHERE activo = 0", nativeQuery = true)
    List<Usuario> findAllInactivos();
}
