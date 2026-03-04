package com.cris959.foro_hub.service;

import com.cris959.foro_hub.dto.DatosRegistroRespuesta;
import com.cris959.foro_hub.dto.DatosRetornoRespuesta;

public interface IRespuestaServiceIA {
    DatosRetornoRespuesta registrar(DatosRegistroRespuesta datos);
}
