package com.cris959.foro_hub.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DatosRegistroUsuario(
        @NotBlank(message = "El nombre no puede estar en blanco")
        String nombre,
        @NotBlank(message = "El correo electrónico es obligatorio")
        @Email(message = "El formato del correo electrónico es inválido")
        String email,
        @NotBlank(message = "La contraseña es obligatoria")
        String password,
        @NotNull(message = "Debe asignar un ID de perfil al usuario")
        Long perfilId // El cliente envía el ID del perfil que quiere asignar
//        Boolean activo
) {
}
