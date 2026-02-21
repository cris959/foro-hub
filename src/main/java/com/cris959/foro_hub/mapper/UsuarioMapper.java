package com.cris959.foro_hub.mapper;

import com.cris959.foro_hub.dto.DatosRespuestaUsuario;
import com.cris959.foro_hub.model.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    private final PerfilMapper perfilMapper;

    public UsuarioMapper(PerfilMapper perfilMapper) {
        this.perfilMapper = perfilMapper;
    }
    public DatosRespuestaUsuario toDatosRespuestaUsuario(Usuario usuario) {
        // 1. Evitamos que el mapper falle si el objeto usuario es nulo
        if (usuario == null) {
            return null;
        }
        return new DatosRespuestaUsuario(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),

                // 2. Verificamos que el perfil no sea nulo antes de pasarlo al PerfilMapper
                usuario.getPerfil() != null
                        ? perfilMapper.toDatosListaPerfil(usuario.getPerfil())
                        : null
        );
    }
}
