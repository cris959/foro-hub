package com.cris959.foro_hub.dto;

import com.cris959.foro_hub.model.Perfil;
import com.cris959.foro_hub.model.PerfilNombre;

public record DatosListaPerfil(
        Long id, PerfilNombre nombre) {

//    public DatosListaPerfil(Perfil perfil) {
//        this(perfil.getId(), perfil.getNombre());
//    }

}
