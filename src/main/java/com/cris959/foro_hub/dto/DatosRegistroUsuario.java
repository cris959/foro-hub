package com.cris959.foro_hub.dto;

public record DatosRegistroUsuario(
        String nombre,
        String email,
        String password,
        Long perfilId // El cliente envía el ID del perfil que quiere asignar
) {
}
