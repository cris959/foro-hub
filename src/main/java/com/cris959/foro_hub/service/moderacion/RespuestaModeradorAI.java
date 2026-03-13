package com.cris959.foro_hub.service.moderacion;

import com.cris959.foro_hub.dto.DatosRegistroRespuesta;
import com.cris959.foro_hub.dto.DatosRetornoRespuesta;
import com.cris959.foro_hub.dto.ResultadoModeracion;
import com.cris959.foro_hub.infra.exception.ValidacionException;
import com.cris959.foro_hub.infra.springai.ModeradorAI;
import com.cris959.foro_hub.model.Respuesta;
import com.cris959.foro_hub.repository.RespuestaRepository;
import com.cris959.foro_hub.repository.TopicoRepository;
import com.cris959.foro_hub.repository.UsuarioRepository;
import com.cris959.foro_hub.service.heuristica.ModeradorHeuristicoLocal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class RespuestaModeradorAI implements IRespuestaModeradorIA {


    private final RespuestaRepository respuestaRepository;

    private final TopicoRepository topicoRepository;

    private final UsuarioRepository usuarioRepository;

    private final ModeradorAI moderadorAI;

    private final ModeradorHeuristicoLocal moderadorLocal;

    public RespuestaModeradorAI(RespuestaRepository respuestaRepository, TopicoRepository topicoRepository, UsuarioRepository usuarioRepository, ModeradorAI moderadorAI, ModeradorHeuristicoLocal moderadorLocal) {
        this.respuestaRepository = respuestaRepository;
        this.topicoRepository = topicoRepository;
        this.usuarioRepository = usuarioRepository;
        this.moderadorAI = moderadorAI;
        this.moderadorLocal = moderadorLocal;
    }

    @Override
    @Transactional
    public DatosRetornoRespuesta registrar(DatosRegistroRespuesta datos) {

        // 1. Verificar existencia
        var topico = topicoRepository.findById(datos.topicoId())
                .orElseThrow(() -> new ValidacionException("El tópico indicado no existe."));
        var autor = usuarioRepository.findById(datos.autorId())
                .orElseThrow(() -> new ValidacionException("El autor indicado no existe."));

        ResultadoModeracion moderacion = moderadorAI.esContenidoOfensivo(datos.mensaje(), autor.getNombre());

        if (moderacion.bloqueado()) {
            // Usa moderacion.fuente() REAL
            throw new ValidacionException(
                    moderacion.fuente() + ": " + moderacion.detalle()  // ← "MISTRAL", no hardcode "GEMINI"
            );
        }

        // 3. CREAR RESPUESTA (JPA Entity)
        var nuevaRespuesta = new Respuesta();
        nuevaRespuesta.setMensaje(datos.mensaje());
        nuevaRespuesta.setFechaCreacion(LocalDateTime.now());
        nuevaRespuesta.setAutor(autor);
        nuevaRespuesta.setTopico(topico);
        nuevaRespuesta.setSolucion(false); // Por defecto

        // 4. GUARDAR EN BASE DE DATOS
        respuestaRepository.save(nuevaRespuesta);

        return new DatosRetornoRespuesta(nuevaRespuesta);
    }
}