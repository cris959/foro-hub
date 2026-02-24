package com.cris959.foro_hub.service;

import com.cris959.foro_hub.dto.DatosActualizarUsuario;
import com.cris959.foro_hub.dto.DatosRegistroUsuario;
import com.cris959.foro_hub.dto.DatosRespuestaUsuario;

import java.util.List;

public interface IUsuarioService {
    DatosRespuestaUsuario registrarUsuario(DatosRegistroUsuario datos);

    DatosRespuestaUsuario obtenerPorId(Long id);

    DatosRespuestaUsuario buscarPorEmail(String email);

    DatosRespuestaUsuario actualizar(DatosActualizarUsuario datos);

    void eliminar(Long id);

    void activar(Long id);

    List<DatosRespuestaUsuario> listarInactivos();
}
