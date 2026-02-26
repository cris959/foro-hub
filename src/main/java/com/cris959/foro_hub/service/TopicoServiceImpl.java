package com.cris959.foro_hub.service;

import com.cris959.foro_hub.dto.DatosActualizarTopico;
import com.cris959.foro_hub.dto.DatosRegistroTopico;
import com.cris959.foro_hub.dto.DatosRespuestaTopico;
import com.cris959.foro_hub.infra.errores.ValidacionException;
import com.cris959.foro_hub.mapper.TopicoMapper;
import com.cris959.foro_hub.model.Topico;
import com.cris959.foro_hub.repository.CursoRepository;
import com.cris959.foro_hub.repository.TopicoRepository;
import com.cris959.foro_hub.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

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
    @Transactional
    public DatosRespuestaTopico crear(DatosRegistroTopico datos) {
        // 1. Validar duplicidad (Uso de tu nuevo método en el Repository)
        if (topicoRepository.existsByTituloAndMensaje(datos.titulo(), datos.mensaje())) {
            throw new ValidacionException("No es posible crear tópicos duplicados. Ya existe un tópico con el mismo título y mensaje.");
        }

        // 2. Validar que el Autor exista (Uso de orElseThrow para evitar el .get() peligroso)
        var autor = usuarioRepository.findById(datos.idAutor())
                .orElseThrow(() -> new EntityNotFoundException("El autor con ID " + datos.idAutor() + " no existe en el sistema."));

        // 3. Validar que el Curso exista
        var curso = cursoRepository.findById(datos.idCurso())
                .orElseThrow(() -> new EntityNotFoundException("El curso con ID " + datos.idCurso() + " no existe."));

        // 4. Instanciar y configurar la Entidad
        Topico topico = new Topico();
        topico.setTitulo(datos.titulo());
        topico.setMensaje(datos.mensaje());
        topico.setAutor(autor);
        topico.setCurso(curso);

        // El estado (StatusTopico) y la fecha de creación se asignan por defecto en la Entidad
        // topico.setActivo(true); // Ya viene por defecto en tu entidad

        // 5. Persistir en la base de datos
        topicoRepository.save(topico);

        // 6. Retornar el DTO usando el Mapper blindado contra nulos
        return topicoMapper.toResponseDTO(topico);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DatosRespuestaTopico> listar(Pageable paginacion) {
        return topicoRepository.findByActivoTrue(paginacion).map(topicoMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public DatosRespuestaTopico buscarPorId(Long id) {
        var topico = topicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Topico no encontrado"));
        return topicoMapper.toResponseDTO(topico);
    }

    @Override
    @Transactional
    public DatosRespuestaTopico actualizar(Long id, DatosActualizarTopico datos) {
        var topico = topicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Topico no encontrado"));

        if (!topico.getActivo()) {
            throw new ValidacionException("No se puede editar un tópico eliminado.");
        }

        // Actualizamos solo lo permitido
        if (datos.titulo() != null && !datos.titulo().isBlank()) {
            topico.setTitulo(datos.titulo());
        }
        if (datos.mensaje() != null && !datos.mensaje().isBlank()) {
            topico.setMensaje(datos.mensaje());
        }

        return topicoMapper.toResponseDTO(topico);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        // 1. Buscamos el tópico incluyendo los que tienen activo = 0
        var topicoOptional = topicoRepository.encontrarEliminadoPorId(id);

        if (topicoOptional.isPresent()) {
            var topico = topicoOptional.get();

            // 2. Si ya está inactivo, no hacemos nada (evita el error 500)
            if (!topico.getActivo()) {
                return; // Aquí podrías lanzar una excepción personalizada si prefieres un 400
            }

            // 3. Si está activo, lo "eliminamos" (borrado lógico)
            topico.setActivo(false);
            // Si usas @SQLDelete, simplemente usa: topicoRepository.delete(topico);
        } else {
            throw new EntityNotFoundException("El tópico con ID " + id + " no existe en la base de datos.");
        }
    }

    @Override
    public void activar(Long id) {
        var topico = topicoRepository.encontrarEliminadoPorId(id)
                .orElseThrow(()-> new EntityNotFoundException("No se encontró el topico con ID: " + id));

        topico.setActivo(true);


    }

    @Override
    @Transactional(readOnly = true)
    public List<DatosRespuestaTopico> listarTodoElHistorial() {

        var todos = topicoRepository.findAll();

        // ESTO ES PARA DEBUG: Mira tu consola de IntelliJ/Eclipse
//        System.out.println("DEBUG: Se encontraron " + todos.size() + " tópicos en la lista.");
//        todos.forEach(t -> System.out.println("ID: " + t.getId() + " | Activo: " + t.getActivo()));

        return todos.stream()
                .map(topicoMapper::toResponseDTO)
                .filter(Objects::nonNull) // Por si algún mapper falló
                .toList();
    }
}
