package com.cris959.foro_hub.service;

import com.cris959.foro_hub.dto.DatosRegistroTopico;
import com.cris959.foro_hub.dto.DatosRespuestaTopico;
import com.cris959.foro_hub.mapper.TopicoMapper;
import com.cris959.foro_hub.model.Topico;
import com.cris959.foro_hub.repository.CursoRepository;
import com.cris959.foro_hub.repository.TopicoRepository;
import com.cris959.foro_hub.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TopicoServiceImpl implements ITopicoService {

    private final TopicoRepository topicoRepository;

    private final UsuarioRepository usuarioRepository;

    private final CursoRepository cursoRepository;

    private final TopicoMapper topicoMapper;

    public TopicoServiceImpl(TopicoRepository topicoRepository, UsuarioRepository usuarioRepository, CursoRepository cursoRepository, TopicoMapper topicoMapper) {
        this.topicoRepository = topicoRepository;
        this.usuarioRepository = usuarioRepository;
        this.cursoRepository = cursoRepository;
        this.topicoMapper = topicoMapper;
    }

    @Override
    public DatosRespuestaTopico registrar(DatosRegistroTopico datos) {
        // Buscar las entidades relacionadas
        var autor = usuarioRepository.findById(datos.idAutor())
                .orElseThrow(() -> new RuntimeException("Autor no encontrado"));
        var curso = cursoRepository.findById(datos.idCurso())
                .orElseThrow(() -> new RuntimeException("Curso no encontrado"));

        // Crear la entidad a partir del DTO
        var topico = new Topico();
        topico.setTitulo(datos.titulo());
        topico.setMensaje(datos.mensaje());
        topico.setAutor(autor);
        topico.setCurso(curso);

        topicoRepository.save(topico);
        return topicoMapper.toResponseDTO(topico);
    }

    @Override
    public Page<DatosRespuestaTopico> listar(Pageable paginacion) {
        return topicoRepository.findByActivoTrue(paginacion).map(topicoMapper::toResponseDTO);
    }

    @Override
    public DatosRespuestaTopico buscarPorId(Long id) {
        var topico = topicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Topico no encontrado"));
        return topicoMapper.toResponseDTO(topico);
    }

    @Override
    public DatosRespuestaTopico actualizar(Long id, DatosRespuestaTopico datos) {
        var topico = topicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Topico no encontrado"));

        topico.setTitulo(datos.titulo());
        topico.setMensaje(datos.mensaje());
        topico.setStatusTopico(datos.statusTopico());

        return topicoMapper.toResponseDTO(topico);
    }

    @Override
    public void eliminar(Long id) {
        // CAMBIO: Buscar el tópico
        var topico = topicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tópico no encontrado"));

        // CAMBIO: Cambiar el estado a inactivo (borrado lógico)
        topico.setActivo(false);

        // Guardar cambios
        topicoRepository.save(topico);
    }
}
