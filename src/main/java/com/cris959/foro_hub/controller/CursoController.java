package com.cris959.foro_hub.controller;

import com.cris959.foro_hub.dto.DatosRegistroCurso;
import com.cris959.foro_hub.dto.DatosRespuestaCurso;
import com.cris959.foro_hub.service.ICursoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/cursos")
public class CursoController {

    private final ICursoService cursoService;

    public CursoController(ICursoService cursoService) {
        this.cursoService = cursoService;
    }

    // POST: Crear un nuevo curso
    @PostMapping
    public ResponseEntity<DatosRespuestaCurso> registrar(@RequestBody @Valid DatosRegistroCurso datos, UriComponentsBuilder uriBuilder) {
        DatosRespuestaCurso cursoResponse = cursoService.registrar(datos);
        var uri = uriBuilder.path("/cursos/{id}").buildAndExpand(cursoResponse.id()).toUri();
        return ResponseEntity.created(uri).body(cursoResponse);
    }

    // GET: Listar cursos activos con paginación
    @GetMapping
    public ResponseEntity<Page<DatosRespuestaCurso>> listar(@PageableDefault(size = 10, sort = {"nombre"}) Pageable paginacion) {
        return ResponseEntity.ok(cursoService.listar(paginacion));
    }

    // GET: Buscar curso por ID
    @GetMapping("/{id}")
    public ResponseEntity<DatosRespuestaCurso> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(cursoService.buscarPorId(id));
    }

    // DELETE: Borrado lógico de un curso
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        cursoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
