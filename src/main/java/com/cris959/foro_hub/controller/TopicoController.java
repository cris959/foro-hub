package com.cris959.foro_hub.controller;

import com.cris959.foro_hub.dto.DatosActualizarTopico;
import com.cris959.foro_hub.dto.DatosRegistroTopico;
import com.cris959.foro_hub.dto.DatosRespuestaTopico;
import com.cris959.foro_hub.service.ITopicoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/topicos")
public class TopicoController {

    private final ITopicoService topicoService;

    public TopicoController(ITopicoService topicoService) {
        this.topicoService = topicoService;
    }

    // POST: Crear un nuevo tópico
    @PostMapping
    public ResponseEntity<DatosRespuestaTopico> registrar(@RequestBody @Valid DatosRegistroTopico datos, UriComponentsBuilder uriBuilder) {
        var topicoResponse = topicoService.crear(datos);
        var uri = uriBuilder.path("/topicos/{id}").buildAndExpand(topicoResponse.id()).toUri();
        return ResponseEntity.created(uri).body(topicoResponse);
    }

    // GET: Listar tópicos con paginación
    @GetMapping
    public ResponseEntity<Page<DatosRespuestaTopico>> listar(@PageableDefault(size = 10, sort = {"fechaCreacion"}) Pageable paginacion) {
        return ResponseEntity.ok(topicoService.listar(paginacion));
    }

    // GET: Buscar un tópico por ID
    @GetMapping("/{id}")
    public ResponseEntity<DatosRespuestaTopico> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(topicoService.buscarPorId(id));
    }

    // PUT: Actualizar un tópico
    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<DatosRespuestaTopico> actualizar(@PathVariable Long id,
                                                           @RequestBody @Valid DatosActualizarTopico datos) {
        var respuesta = topicoService.actualizar(id, datos);
        return ResponseEntity.ok(respuesta);
    }

    // DELETE: Eliminar un tópico
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        topicoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/historial")
    public ResponseEntity<List<DatosRespuestaTopico>> listarActivoEInactivos() {
        return ResponseEntity.ok(topicoService.listarTodoElHistorial());
    }

    @PostMapping("/{id}/activar")
    @Transactional
    public ResponseEntity activar(@PathVariable Long id) {
        topicoService.activar(id);
        return ResponseEntity.noContent().build();
    }
}
