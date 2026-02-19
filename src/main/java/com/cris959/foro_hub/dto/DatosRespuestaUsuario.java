package com.cris959.foro_hub.dto;

import com.cris959.foro_hub.model.Usuario;

public record DatosRespuestaUsuario(
        Long id,
        String nombre,
        String email,
        DatosListaPerfil perfil // Usamos el DTO de perfil que creamos antes
) {
    public DatosRespuestaUsuario(Usuario usuario) {
        this(usuario.getId(), usuario.getNombre(), usuario.getEmail(),
                new DatosListaPerfil(usuario.getPerfil()));
    }
}
