package com.cris959.foro_hub.mapper;

import com.cris959.foro_hub.dto.DatosListaPerfil;
import com.cris959.foro_hub.model.Perfil;
import org.springframework.stereotype.Component;

@Component
public class PerfilMapper {

    public DatosListaPerfil toDatosListaPerfil(Perfil perfil) {
        return new DatosListaPerfil(
                perfil.getId(),
                perfil.getNombre()
        );
    }
}
