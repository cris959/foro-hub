package com.cris959.foro_hub.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DatosRegistroUsuario(
        @NotBlank String nombre,
        @NotBlank @Email String email,
        @NotBlank String password,
        @NotNull Long perfilId // El cliente envía el ID del perfil que quiere asignar
) {
}
