package com.cris959.foro_hub.service;

import com.cris959.foro_hub.dto.DatosRegistroUsuario;
import com.cris959.foro_hub.dto.DatosRespuestaUsuario;
import com.cris959.foro_hub.mapper.UsuarioMapper;
import com.cris959.foro_hub.model.Usuario;
import com.cris959.foro_hub.repository.PerfilRepository;
import com.cris959.foro_hub.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioServiceImpl implements IUsuarioService{

    private final UsuarioRepository usuarioRepository;

    private final PerfilRepository perfilRepository;

    private final UsuarioMapper usuarioMapper;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, PerfilRepository perfilRepository, UsuarioMapper usuarioMapper) {
        this.usuarioRepository = usuarioRepository;
        this.perfilRepository = perfilRepository;
        this.usuarioMapper = usuarioMapper;
    }


    @Override
    public DatosRespuestaUsuario registrarUsuario(DatosRegistroUsuario datos) {
        var perfil = perfilRepository.findById(datos.perfilId())
                .orElseThrow(() -> new EntityNotFoundException("Perfil no encontrado"));

        Usuario usuario = new Usuario();
        usuario.setNombre(datos.nombre());
        usuario.setEmail(datos.email());
        usuario.setPassword(datos.password());
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
}
