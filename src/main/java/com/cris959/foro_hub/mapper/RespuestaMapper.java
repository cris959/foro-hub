package com.cris959.foro_hub.mapper;

import com.cris959.foro_hub.dto.DatosRetornoRespuesta;
import com.cris959.foro_hub.model.Respuesta;
import org.springframework.stereotype.Component;

@Component
public class RespuestaMapper {

        public DatosRetornoRespuesta toDatosRetorno(Respuesta respuesta) {
            return new DatosRetornoRespuesta(
                    respuesta.getId(),
                    respuesta.getMensaje(),
                    respuesta.getFechaCreacion(),
                    respuesta.getAutor().getNombre(),
                    respuesta.getTopico().getTitulo()
            );
        }
}
