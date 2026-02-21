package com.cris959.foro_hub.mapper;

import com.cris959.foro_hub.dto.DatosRetornoRespuesta;
import com.cris959.foro_hub.model.Respuesta;
import org.springframework.stereotype.Component;

@Component
public class RespuestaMapper {

        public DatosRetornoRespuesta toDatosRetorno(Respuesta respuesta) {
            if (respuesta == null) {
                return null;
            }
            return new DatosRetornoRespuesta(
                    respuesta.getId(),
                    respuesta.getMensaje(),
                    respuesta.getFechaCreacion(),
                    // Verificación de nulidad para el Autor
                    respuesta.getAutor() != null ? respuesta.getAutor().getNombre() : "Autor no disponible",
                    // Verificación de nulidad para el Tópico
                    respuesta.getTopico() != null ? respuesta.getTopico().getTitulo() : "Tópico no disponible"
            );
        }
}
