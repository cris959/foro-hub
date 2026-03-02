package com.cris959.foro_hub.dto;

import com.cris959.foro_hub.model.Perfil;
import com.cris959.foro_hub.model.PerfilNombre;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos de perfil para respuestas")
public record DatosListaPerfil(

        @Schema(example = "1")
        Long id,

        @Schema(example = "ADMIN")
        PerfilNombre nombre) {

    public DatosListaPerfil(Perfil perfil) {
        this(perfil.getId(), perfil.getNombre());
    }

}
