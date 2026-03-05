package com.cris959.foro_hub.service.moderacion;

import com.cris959.foro_hub.dto.DatosRegistroRespuesta;
import com.cris959.foro_hub.dto.DatosRetornoRespuesta;

public interface IRespuestaModeradorIA {
    DatosRetornoRespuesta registrar(DatosRegistroRespuesta datos);
}
