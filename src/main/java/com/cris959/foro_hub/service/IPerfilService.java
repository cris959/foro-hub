package com.cris959.foro_hub.service;

import com.cris959.foro_hub.dto.DatosListaPerfil;
import com.cris959.foro_hub.dto.DatosRegistroPerfil;
import com.cris959.foro_hub.model.PerfilNombre;

import java.util.List;

public interface IPerfilService {

    List<DatosListaPerfil> listarPerfiles();

    DatosListaPerfil buscarPorNombre(PerfilNombre nombre);

    DatosListaPerfil guardar(DatosRegistroPerfil datos);
}

