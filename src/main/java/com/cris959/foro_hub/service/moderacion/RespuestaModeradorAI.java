package com.cris959.foro_hub.service.moderacion;

import com.cris959.foro_hub.dto.DatosRegistroRespuesta;
import com.cris959.foro_hub.dto.DatosRetornoRespuesta;
import com.cris959.foro_hub.infra.exception.ValidacionException;
import com.cris959.foro_hub.infra.springai.ModeradorAI;
import com.cris959.foro_hub.model.Respuesta;
import com.cris959.foro_hub.repository.RespuestaRepository;
import com.cris959.foro_hub.repository.TopicoRepository;
import com.cris959.foro_hub.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        // 1. Primero verificamos que el Tópico y el Autor existan (necesitamos al autor para moderar)
        var topico = topicoRepository.findById(datos.topicoId())
                .orElseThrow(() -> new ValidacionException("El tópico indicado no existe."));

        var autor = usuarioRepository.findById(datos.autorId())
                .orElseThrow(() -> new ValidacionException("El autor indicado no existe."));

        // 2. Ahora que ya tenemos el objeto 'autor', hacemos la Validación de Seguridad
        // Usamos el bloque try-catch para que el respaldo local funcione si Gemini falla
        boolean esOfensivo = false;
        try {
            esOfensivo = moderadorAI.esContenidoOfensivo(datos.mensaje(), autor.getNombre());
        } catch (Exception e) {
            System.out.println("IA fuera de línea, usando moderador local...");
            var resultadoLocal = moderadorLocal.analizarTexto(datos.mensaje(), autor.getNombre());
            esOfensivo = resultadoLocal.bloqueado();
        }

        if (esOfensivo) {
            throw new ValidacionException("La respuesta contiene contenido no permitido (Insultos, Spam o Autor sospechoso).");
        }

        // 3. Creación de la entidad (Si pasó la moderación)
        var respuesta = new Respuesta(datos.mensaje(), topico, autor);

        // 4. Persistencia
        respuestaRepository.save(respuesta);

        // 5. Retorno
        return new DatosRetornoRespuesta(respuesta);
    }
}
