package com.cris959.foro_hub.dto;

import com.cris959.foro_hub.model.PerfilNombre;
import jakarta.validation.constraints.NotNull;

public record DatosRegistroPerfil(
        @NotNull PerfilNombre nombre
        ) {
}
