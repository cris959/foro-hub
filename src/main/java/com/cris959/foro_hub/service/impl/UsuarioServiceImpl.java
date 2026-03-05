package com.cris959.foro_hub.service.impl;

import com.cris959.foro_hub.dto.DatosActualizarUsuario;
import com.cris959.foro_hub.dto.DatosRegistroUsuario;
import com.cris959.foro_hub.dto.DatosRespuestaUsuario;
import com.cris959.foro_hub.infra.exception.ValidacionException;
import com.cris959.foro_hub.mapper.UsuarioMapper;
import com.cris959.foro_hub.model.Usuario;
import com.cris959.foro_hub.repository.PerfilRepository;
import com.cris959.foro_hub.repository.UsuarioRepository;
import com.cris959.foro_hub.service.IUsuarioService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioServiceImpl implements IUsuarioService {

    private final UsuarioRepository usuarioRepository;

    private final PerfilRepository perfilRepository;

    private final UsuarioMapper usuarioMapper;

    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, PerfilRepository perfilRepository, UsuarioMapper usuarioMapper, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.perfilRepository = perfilRepository;
        this.usuarioMapper = usuarioMapper;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    @Transactional
    public DatosRespuestaUsuario registrarUsuario(DatosRegistroUsuario datos) {
        // Validación de email único
        if (usuarioRepository.countByEmailNative(datos.email()) > 0) {
            throw new ValidacionException("Ya existe un usuario registrado con este correo electrónico.");
        }

        var perfil = perfilRepository.findById(datos.perfilId())
                .orElseThrow(() -> new EntityNotFoundException("El perfil con ID " + datos.perfilId() + " no existe en el sistema."));

        Usuario usuario = new Usuario();
        usuario.setNombre(datos.nombre());
        usuario.setEmail(datos.email());
        usuario.setPassword(passwordEncoder.encode(datos.password()));
        usuario.setPerfil(perfil);

        usuarioRepository.save(usuario);

        // Usamos el mapper para la respuesta
        return usuarioMapper.toDatosRespuestaUsuario(usuario);
    }

    @Override
    @Transactional(readOnly = true) // Optimizamos para solo lectura
    public DatosRespuestaUsuario obtenerPorId(Long id) {
        // 1. Buscamos el usuario en el repositorio
        return usuarioRepository.findById(id)
                // 2. Si no existe, lanzamos una excepción clara
                .map(usuarioMapper::toDatosRespuestaUsuario)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró el usuario con el ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public DatosRespuestaUsuario buscarPorEmail(String email) {
        var usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró un usuario con el email: " + email));

        return usuarioMapper.toDatosRespuestaUsuario(usuario);
    }

    @Override
    @Transactional
    public DatosRespuestaUsuario actualizar(Long id, DatosActualizarUsuario datos) {
        // 1. Buscamos al usuario o lanzamos error si no existe (404)
        var usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ValidacionException("Usuario no encontrado"));

        // 2. Actualizamos solo los campos que no vienen nulos en el DTO
        if (datos.nombre() != null && !datos.nombre().trim().isBlank()) {
            usuario.setNombre(datos.nombre().trim());
        }

        if (datos.password() != null && !datos.password().trim().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(datos.password().trim()));
        }

        if (datos.perfilId() != null) {
            var perfil = perfilRepository.findById(datos.perfilId())
                    .orElseThrow(() -> new ValidacionException("El perfil asignado no existe"));
            usuario.setPerfil(perfil);
        }

        // 3. Convertimos la entidad actualizada a DTO de respuesta
        usuario.setActivo(datos.activo());
        return usuarioMapper.toDatosRespuestaUsuario(usuarioRepository.save(usuario));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
// 1. Buscamos al usuario (si no existe, lanza 404)
        var usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró el usuario con ID: " + id));

        // 2. Aplicamos la eliminación lógica
        usuario.setActivo(false);

        // Nota: Gracias a @Transactional, el cambio se guarda solo al final del procedimiento.
    }

    @Override
    @Transactional
    public void activar(Long id) {
// Usamos el procedimiento nativo para encontrar al usuario aunque tenga activo = 0
        var usuario = usuarioRepository.encontrarEliminadoPorId(id)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró el usuario con ID: " + id));

        // Cambiamos el estado a true
        usuario.setActivo(true);

        // No hace falta llamar a save() por el @Transactional (Dirty Checking)
    }

    @Override
    @Transactional(readOnly = true)
    public List<DatosRespuestaUsuario> listarInactivos() {
        return usuarioRepository.findAllInactivos()
                .stream()
                .map(usuarioMapper::toDatosRespuestaUsuario)
                .toList();
    }
}
