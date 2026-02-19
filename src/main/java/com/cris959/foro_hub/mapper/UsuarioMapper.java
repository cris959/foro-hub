package com.cris959.foro_hub.mapper;

import com.cris959.foro_hub.dto.DatosRespuestaUsuario;
import com.cris959.foro_hub.model.Usuario;

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
                perfilMapper.toDatosListaPerfil(usuario.getPerfil())
        );
    }
}
