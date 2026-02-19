package com.cris959.foro_hub.service;

import com.cris959.foro_hub.dto.DatosRegistroUsuario;
import com.cris959.foro_hub.dto.DatosRespuestaUsuario;
import com.cris959.foro_hub.mapper.UsuarioMapper;
import com.cris959.foro_hub.model.Usuario;
import com.cris959.foro_hub.repository.PerfilRepository;
import com.cris959.foro_hub.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

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
}
