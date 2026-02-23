package com.cris959.foro_hub.service;

import com.cris959.foro_hub.dto.DatosActualizarUsuario;
import com.cris959.foro_hub.dto.DatosRegistroUsuario;
import com.cris959.foro_hub.dto.DatosRespuestaUsuario;

public interface IUsuarioService {
    DatosRespuestaUsuario registrarUsuario(DatosRegistroUsuario datos);

    DatosRespuestaUsuario obtenerPorId(Long id);

    DatosRespuestaUsuario buscarPorEmail(String email);

    DatosRespuestaUsuario actualizar(DatosActualizarUsuario datos);
}
