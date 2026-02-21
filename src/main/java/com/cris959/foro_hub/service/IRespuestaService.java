package com.cris959.foro_hub.service;

import com.cris959.foro_hub.dto.DatosRegistroRespuesta;
import com.cris959.foro_hub.dto.DatosRetornoRespuesta;

import java.util.List;

public interface IRespuestaService {

    DatosRetornoRespuesta registar(DatosRegistroRespuesta datos);

    List<DatosRetornoRespuesta> listarPorTopico(Long topicoId);
}
