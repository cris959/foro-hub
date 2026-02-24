package com.cris959.foro_hub.repository;

import com.cris959.foro_hub.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // 1. Reemplaza a findByEmail y findByEmailActivo.
    // Por convención de Spring Data, ya filtrará por activo=1 automáticamente.
    Optional<Usuario> findByEmail(String email);

    // Ahora este procedimiento verá el TOTAL de DB, incluso lo que @Where oculta
//    @Query(value = "SELECT COUNT(*) > 0 FROM usuarios WHERE email = :email", nativeQuery = true)
//    boolean existsByEmail(String email);

    // En UsuarioRepository.java
    @Query(value = "SELECT COUNT(*) FROM usuarios WHERE email = :email", nativeQuery = true)
    Long countByEmailNative(String email);

    // 3. NECESARIO: Porque se salta el filtro @Where para poder reactivar
    @Query(value = "SELECT * FROM usuarios WHERE id = :id", nativeQuery = true)
    Optional<Usuario> encontrarEliminadoPorId(Long id);

    // 4. NECESARIO: Porque busca específicamente lo que el filtro @Where oculta
    @Query(value = "SELECT * FROM usuarios WHERE activo = 0", nativeQuery = true)
    List<Usuario> findAllInactivos();
}
