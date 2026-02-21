package com.cris959.foro_hub.service;

import com.cris959.foro_hub.dto.DatosRegistroRespuesta;
import com.cris959.foro_hub.dto.DatosRetornoRespuesta;
import com.cris959.foro_hub.mapper.RespuestaMapper;
import com.cris959.foro_hub.model.Respuesta;
import com.cris959.foro_hub.repository.RespuestaRepository;
import com.cris959.foro_hub.repository.TopicoRepository;
import com.cris959.foro_hub.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RespuestaServiceImpl implements IRespuestaService{

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
    public DatosRetornoRespuesta registar(DatosRegistroRespuesta datos) {
        // 1. Buscas entidades (se abre la sesión de BD)
        var autor = usuarioRepository.findById(datos.autorId())
                .orElseThrow(() -> new EntityNotFoundException("No se encontró el usuario con el ID proporcionado"));
        var topico = topicoRepository.findById(datos.topicoId())
                .orElseThrow(() -> new EntityNotFoundException("No se encontró el tópico proporcionado"));

        // 2. Creas la respuesta
        Respuesta respuesta = new Respuesta();
        respuesta.setMensaje(datos.mensaje());
        respuesta.setAutor(autor);
        respuesta.setTopico(topico);

        // 3. Guardas
        respuestaRepository.save(respuesta);

        // 4. El Mapper accede a autor.getNombre() y topico.getTitulo()
        // Gracias a @Transactional, la sesión sigue abierta y el LAZY funciona.
        return respuestaMapper.toDatosRetorno(respuesta);
    } // Al salir del método, se hace el COMMIT a la base de datos.

    @Override
    @Transactional(readOnly = true) // Usamos readOnly para optimizar consultas de lectura
    public List<DatosRetornoRespuesta> listarPorTopico(Long topicoId) {
        return respuestaRepository.findAllByTopicoId(topicoId)
                .stream()
                .map(respuestaMapper::toDatosRetorno)
                .toList();
    }
}
