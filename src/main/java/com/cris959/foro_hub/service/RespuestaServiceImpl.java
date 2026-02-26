package com.cris959.foro_hub.service;

import com.cris959.foro_hub.dto.DatosRegistroRespuesta;
import com.cris959.foro_hub.dto.DatosRetornoRespuesta;
import com.cris959.foro_hub.infra.errores.ValidacionException;
import com.cris959.foro_hub.mapper.RespuestaMapper;
import com.cris959.foro_hub.model.Respuesta;
import com.cris959.foro_hub.model.StatusTopico;
import com.cris959.foro_hub.repository.RespuestaRepository;
import com.cris959.foro_hub.repository.TopicoRepository;
import com.cris959.foro_hub.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
public class RespuestaServiceImpl implements IRespuestaService {

    private final RespuestaRepository respuestaRepository;
    private final UsuarioRepository usuarioRepository;
    private final TopicoRepository topicoRepository;
    private final RespuestaMapper respuestaMapper;

    public RespuestaServiceImpl(RespuestaRepository respuestaRepository, UsuarioRepository usuarioRepository, TopicoRepository topicoRepository, RespuestaMapper respuestaMapper) {
        this.respuestaRepository = respuestaRepository;
        this.usuarioRepository = usuarioRepository;
        this.topicoRepository = topicoRepository;
        this.respuestaMapper = respuestaMapper;
    }


    @Override
    @Transactional
    public DatosRetornoRespuesta registrar(DatosRegistroRespuesta datos) {
        // 1. Validar que el autor existe
        var autor = usuarioRepository.findById(datos.autorId())
                .orElseThrow(() -> new EntityNotFoundException("No se encontró el usuario con el ID proporcionado"));
        // 2. Validar que el tópico existe y está activo
        var topico = topicoRepository.findById(datos.topicoId())
                .orElseThrow(() -> new EntityNotFoundException("No se encontró el tópico proporcionado"));

        if (!topico.getActivo()) {
            throw new ValidacionException("No se pueden añadir respuestas a un tópico eliminado.");
        }
        // 3. Crear y guardar la respuesta
        var respuesta = new Respuesta();
        respuesta.setMensaje(datos.mensaje());
        respuesta.setAutor(autor);
        respuesta.setTopico(topico);

        respuestaRepository.save(respuesta);

        return respuestaMapper.toDatosRetorno(respuesta);
    }

//    @Override
//    @Transactional(readOnly = true) // Usamos readOnly para optimizar consultas de lectura
//    public Page<DatosRetornoRespuesta> listarPorTopico(Long topicoId, Pageable paginacion) {
//
//        // Buscamos solo las respuestas que pertenecen a ese ID de tópico
//        return respuestaRepository.findByTopicoId(topicoId, paginacion)
//                .map(respuestaMapper::toDatosRetorno);
//    }

    @Override
    @Transactional
    public void marcarComoSolucion(Long id) {
        // 1. Validar existencia
        var respuesta = respuestaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró la respuesta con ID: " + id));

        // 2. Lógica de negocio: Limpiar soluciones previas en el mismo tópico
        var topicoId = respuesta.getTopico().getId();
        respuestaRepository.desmarcarOtrasSoluciones(topicoId);

        // 3. Marcar nueva solución
        respuesta.setSolucion(true);

        // 4. Actualizar el estado del tópico relacionado
        var topico = respuesta.getTopico();
        topico.setStatusTopico(StatusTopico.SOLUCIONADO);

        // Gracias a @Transactional, los cambios en 'respuesta' y 'topico'
        // se sincronizan con la DB automáticamente.
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DatosRetornoRespuesta> listarPorTopico(Long topicoId, Pageable paginacion) {
        // 1. Verificamos que el tópico existe para dar un error claro si el ID está mal
        if (!topicoRepository.existsById(topicoId)) {
            throw new EntityNotFoundException("El tópico solicitado no existe.");
        }

        // 2. El repositorio ahora debe recibir el objeto 'paginacion'
        return respuestaRepository.findAllByTopicoId(topicoId, paginacion)
                .map(respuestaMapper::toDatosRetorno);
    }
}