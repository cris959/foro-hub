package com.cris959.foro_hub.service;

import com.cris959.foro_hub.dto.DatosRegistroRespuesta;
import com.cris959.foro_hub.dto.DatosRetornoRespuesta;
import com.cris959.foro_hub.infra.exception.ValidacionException;
import com.cris959.foro_hub.infra.springai.ModeradorAI;
import com.cris959.foro_hub.model.Respuesta;
import com.cris959.foro_hub.repository.RespuestaRepository;
import com.cris959.foro_hub.repository.TopicoRepository;
import com.cris959.foro_hub.repository.UsuarioRepository;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RespuestaService implements IRespuestaServiceIA{


    private final RespuestaRepository respuestaRepository;

    private final TopicoRepository topicoRepository;

    private final UsuarioRepository usuarioRepository;

    private final ModeradorAI moderadorAI;

    public RespuestaService(RespuestaRepository respuestaRepository, TopicoRepository topicoRepository, UsuarioRepository usuarioRepository, ModeradorAI moderadorAI) {
        this.respuestaRepository = respuestaRepository;
        this.topicoRepository = topicoRepository;
        this.usuarioRepository = usuarioRepository;
        this.moderadorAI = moderadorAI;
    }

    @Override
    @Transactional
    public DatosRetornoRespuesta registrar(DatosRegistroRespuesta datos) {
        // 1. Validación de Seguridad con Gemini
        if (moderadorAI.esContenidoOfensivo(datos.mensaje())) {
            throw new ValidacionException("La respuesta contiene lenguaje no permitido por las políticas del foro.");
        }

        // 2. Verificación de existencia de Tópico y Autor
        var topico = topicoRepository.findById(datos.topicoId())
                .orElseThrow(() -> new ValidacionException("El tópico indicado no existe."));

        var autor = usuarioRepository.findById(datos.autorId())
                .orElseThrow(() -> new ValidacionException("El autor indicado no existe."));

        // 3. Creación de la entidad
        var respuesta = new Respuesta(datos.mensaje(), topico, autor);

        // 4. Persistencia
        respuestaRepository.save(respuesta);

        // 5. Retorno del DTO de salida
        return new DatosRetornoRespuesta(respuesta);
    }
}
