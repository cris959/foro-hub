package com.cris959.foro_hub.service.impl;

import com.cris959.foro_hub.dto.DatosActualizarTopico;
import com.cris959.foro_hub.dto.DatosRegistroTopico;
import com.cris959.foro_hub.dto.DatosRespuestaTopico;
import com.cris959.foro_hub.dto.ResultadoModeracion;
import com.cris959.foro_hub.infra.exception.ValidacionException;
import com.cris959.foro_hub.infra.springai.ModeradorAI;
import com.cris959.foro_hub.mapper.TopicoMapper;
import com.cris959.foro_hub.model.Topico;
import com.cris959.foro_hub.repository.CursoRepository;
import com.cris959.foro_hub.repository.TopicoRepository;
import com.cris959.foro_hub.repository.UsuarioRepository;
import com.cris959.foro_hub.service.ITopicoService;
import com.cris959.foro_hub.service.moderacion.ModeradorHeuristicoLocal;
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

    private final ModeradorHeuristicoLocal moderadorLocal;

    private final ModeradorAI moderadorAI;

    public TopicoServiceImpl(TopicoRepository topicoRepository, UsuarioRepository usuarioRepository, CursoRepository cursoRepository, TopicoMapper topicoMapper, ModeradorHeuristicoLocal moderadorLocal, ModeradorAI moderadorAI) {
        this.topicoRepository = topicoRepository;
        this.usuarioRepository = usuarioRepository;
        this.cursoRepository = cursoRepository;
        this.topicoMapper = topicoMapper;
        this.moderadorLocal = moderadorLocal;
        this.moderadorAI = moderadorAI;
    }

    @Override
    @Transactional
    public DatosRespuestaTopico crear(DatosRegistroTopico datos) {
        // 1. Validar duplicidad (Uso de tu nuevo procedimiento en el Repository)
        if (topicoRepository.existsByTituloAndMensaje(datos.titulo(), datos.mensaje())) {
            throw new ValidacionException("No es posible crear tópicos duplicados. Ya existe un tópico con el mismo título y mensaje.");
        }

        // 2. Validar que el Autor exista (Uso de orElseThrow para evitar el .get() peligroso)
        var autor = usuarioRepository.findById(datos.idAutor())
                .orElseThrow(() -> new EntityNotFoundException("El autor con ID " + datos.idAutor() + " no existe en el sistema."));

        // 3. Validar que el Curso exista
        var curso = cursoRepository.findById(datos.idCurso())
                .orElseThrow(() -> new EntityNotFoundException("El curso con ID " + datos.idCurso() + " no existe."));

        // 3.5. Moderación con Inteligencia Artificial (y respaldo Local)
        boolean bloqueadoFinal = false;
        String motivoFinal = "";

        try {
//            System.out.println("SISTEMA: Intentando moderación con AI...");
            bloqueadoFinal = moderadorAI.esContenidoOfensivo(datos.mensaje(), String.valueOf(datos.idAutor()));
            motivoFinal = "Bloqueado por Inteligencia Artificial (Gemini)";
        } catch (Exception e) {
            // ESTO SE EJECUTA SI NO HAY INTERNET
//            System.out.println("ALERTA: IA fuera de línea. Entrando al RESPALDO LOCAL...");
            ResultadoModeracion resultadoLocal = moderadorLocal.analizarTexto(datos.mensaje(), autor.getNombre());
            bloqueadoFinal = resultadoLocal.bloqueado();
            motivoFinal = "Bloqueado por Filtro Local. " + resultadoLocal.detalle();
        }

        // ESTA ES LA LÍNEA MÁS IMPORTANTE: Detiene el proceso si algo falló
        if (bloqueadoFinal) {
//            System.out.println("SISTEMA: Publicación RECHAZADA por: " + motivoFinal);
            throw new ValidacionException("No se puede publicar: " + motivoFinal);
        }

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
                .orElseThrow(() -> new ValidacionException("Tópico no encontrado con ID: " + id));

        if (!topico.getActivo()) {
            throw new ValidacionException("No se puede editar un tópico inactivo.");
        }


        // Actualización SIMPLE (usa @NotBlank del DTO)
        if (datos.titulo() != null && !datos.titulo().trim().isBlank()) {
            topico.setTitulo(datos.titulo().trim());
        }
        if (datos.mensaje() != null && !datos.mensaje().trim().isBlank()) {
            topico.setMensaje(datos.mensaje().trim());
        }

        // Estado siempre (DTO tiene @NotNull)
        topico.setActivo(datos.activo());

        topicoRepository.save(topico);
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
                .orElseThrow(() -> new EntityNotFoundException("No se encontró el topico con ID: " + id));

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
