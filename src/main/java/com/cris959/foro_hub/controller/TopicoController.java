package com.cris959.foro_hub.controller;

import com.cris959.foro_hub.dto.DatosRegistroTopico;
import com.cris959.foro_hub.dto.DatosRespuestaTopico;
import com.cris959.foro_hub.model.Topico;
import com.cris959.foro_hub.service.ITopicoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

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
        var topicoResponse = topicoService.registrar(datos);
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
    public ResponseEntity<DatosRespuestaTopico> actualizar(@PathVariable Long id, @RequestBody @Valid DatosRespuestaTopico datos) {
        return ResponseEntity.ok(topicoService.actualizar(id, datos));
    }

    // DELETE: Eliminar un tópico
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        topicoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
