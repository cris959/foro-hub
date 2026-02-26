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
        // 1. Verificamos si el tópico existe
        var topico = topicoRepository.findById(datos.topicoId())
                .orElseThrow(() -> new EntityNotFoundException("El tópico no existe"));

        // 2. VALIDACIÓN DE DUPLICADOS:
        // Comprobamos si ya existe exactamente el mismo mensaje en este tópico
        boolean existeDuplicado = respuestaRepository.existsByTopicoIdAndMensaje(datos.topicoId(), datos.mensaje());

        if (existeDuplicado) {
            throw new ValidacionException("No se permiten respuestas duplicadas en el mismo tópico.");
        }

        // 3. Si no es duplicado, procedemos con el registro normal
        var autor = usuarioRepository.findById(datos.autorId())
                .orElseThrow(() -> new EntityNotFoundException("El autor no existe"));

        var respuesta = new Respuesta(datos.mensaje(), topico, autor);
        respuestaRepository.save(respuesta);

        return new DatosRetornoRespuesta(respuesta);
    }

    @Override
    @Transactional
    public void marcarComoSolucion(Long id) {
        var respuesta = respuestaRepository.findByIdWithTopico(id)
                .orElseThrow(() -> new EntityNotFoundException("Respuesta no encontrada"));

        var topico = respuesta.getTopico();

        // VALIDACIÓN 1: Verificar si la respuesta ya es la solución
        if (Boolean.TRUE.equals(respuesta.getSolucion())) {
            throw new ValidacionException("Esta respuesta ya ha sido marcada como la solución.");
        }

        // 1. Ejecutar el update masivo
        respuestaRepository.desmarcarOtrasSoluciones(topico.getId());

        // 2. Modificar los estados en memoria
        respuesta.setSolucion(true);
        topico.setStatusTopico(StatusTopico.SOLUCIONADO);
        // 3. FORZAR la persistencia inmediata
        respuestaRepository.save(respuesta);
        topicoRepository.save(topico); // Asegúrate de tener inyectado topicoRepository
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DatosRetornoRespuesta> listarTodas(Pageable paginacion) {
        return respuestaRepository.findAll(paginacion).map(DatosRetornoRespuesta::new);
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