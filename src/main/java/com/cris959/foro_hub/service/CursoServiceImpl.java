package com.cris959.foro_hub.service;

import com.cris959.foro_hub.dto.DatosRegistroCurso;
import com.cris959.foro_hub.dto.DatosRespuestaCurso;
import com.cris959.foro_hub.infra.errores.ValidacionException;
import com.cris959.foro_hub.mapper.CursoMapper;
import com.cris959.foro_hub.model.Curso;
import com.cris959.foro_hub.repository.CursoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CursoServiceImpl implements ICursoService{


    private final CursoRepository cursoRepository;

    private final CursoMapper cursoMapper;

    public CursoServiceImpl(CursoRepository cursoRepository, CursoMapper cursoMapper) {
        this.cursoRepository = cursoRepository;
        this.cursoMapper = cursoMapper;
    }


    @Override
    @Transactional
    public DatosRespuestaCurso registrar(DatosRegistroCurso datos) {
//        var curso = new Curso();
//        curso.setNombre(datos.nombre());
//        curso.setCategoria(datos.categoria());
//        curso.setActivo(true);
//
//        cursoRepository.save(curso);
//        return cursoMapper.toResponseDTO(curso);
        // Validamos antes de intentar insertar
        if (cursoRepository.existsByNombreAndCategoria(datos.nombre(), datos.categoria())) {
            throw new ValidacionException("Ya existe un curso con ese nombre.");
        }

        // 2. Convertir DTO a Entidad y Guardar
        // Si no tienes el constructor en la entidad, puedes usar los setters:
        Curso curso = new Curso();
        curso.setNombre(datos.nombre());
        curso.setCategoria(datos.categoria());
        curso.setActivo(true);

        cursoRepository.save(curso);

        // 3. Convertir Entidad a DTO de respuesta usando tu Mapper
        return cursoMapper.toResponseDTO(curso);
    }

    @Override
    public Page<DatosRespuestaCurso> listar(Pageable paginacion) {
        return cursoRepository.findByActivoTrue(paginacion).map(cursoMapper::toResponseDTO);
    }

    @Override
    public DatosRespuestaCurso buscarPorId(Long id) {
        var curso = cursoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado"));
        return cursoMapper.toResponseDTO(curso);
    }

    @Override
    public void eliminar(Long id) {
        var curso = cursoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado"));

        // Borrado lógico
        curso.setActivo(false);
        cursoRepository.save(curso);
    }

}
