package com.cris959.foro_hub.dto;

import com.cris959.foro_hub.model.PerfilNombre;
import jakarta.validation.constraints.NotNull;

public record DatosRegistroPerfil(
        @NotNull(message = "El nombre del perfil es obligatorio (ej: ROLE_ADMIN, ROLE_USER)")
        PerfilNombre nombre
        ) {
}
