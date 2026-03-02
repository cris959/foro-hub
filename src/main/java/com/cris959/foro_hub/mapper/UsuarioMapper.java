package com.cris959.foro_hub.mapper;

import com.cris959.foro_hub.dto.DatosRespuestaUsuario;
import com.cris959.foro_hub.model.Usuario;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UsuarioMapper {

    private final PerfilMapper perfilMapper;

    public UsuarioMapper(PerfilMapper perfilMapper) {
        this.perfilMapper = perfilMapper;
    }
    public DatosRespuestaUsuario toDatosRespuestaUsuario(Usuario usuario) {
        return new DatosRespuestaUsuario(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                Optional.ofNullable(usuario.getPerfil())
                        .map(perfilMapper::toDatosListaPerfil)
                        .orElse(null)
        );
    }
}
