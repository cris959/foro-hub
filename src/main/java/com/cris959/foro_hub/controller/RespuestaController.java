package com.cris959.foro_hub.controller;

import com.cris959.foro_hub.dto.DatosRegistroRespuesta;
import com.cris959.foro_hub.dto.DatosRetornoRespuesta;
import com.cris959.foro_hub.service.IRespuestaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/respuestas")
public class RespuestaController {

    private final IRespuestaService respuestaService;

    public RespuestaController(IRespuestaService respuestaService) {
        this.respuestaService = respuestaService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<DatosRetornoRespuesta> registrar(@RequestBody @Valid DatosRegistroRespuesta datos, UriComponentsBuilder uriComponentsBuilder) {
        DatosRetornoRespuesta respuesta = respuestaService.registrar(datos);

        // Creamos la URL dinámica para la nueva respuesta
        URI url = uriComponentsBuilder.path("/respuestas/{id}").buildAndExpand(respuesta.id()).toUri();
        System.out.println("Entrando al método registrar respuesta...");
        return ResponseEntity.created(url).body(respuesta);
    }

    @PutMapping("/{id}/solucion")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Transactional
    public ResponseEntity marcarComoSolucion(@PathVariable Long id) {
        respuestaService.marcarComoSolucion(id);
        // Aquí llamarías a un procedimiento del service que cambie el boolean 'solucion' a true
        return ResponseEntity.ok("La respuesta ha sido marcada como la solución definitiva!");
    }

    @GetMapping("/respuesta/{topicoId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Page<DatosRetornoRespuesta>> listarPorTopico(@PathVariable Long topicoId,
                                                                       @PageableDefault(size = 10, sort = "fechaCreacion", direction = Sort.Direction.ASC) Pageable paginacion) {
        var pagina = respuestaService.listarPorTopico(topicoId, paginacion);
        return ResponseEntity.ok(pagina);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Page<DatosRetornoRespuesta>> listarTodasLasRespuestas(
            @PageableDefault(size = 10, sort = "fechaCreacion", direction = Sort.Direction.DESC) Pageable paginacion) {
        var todasLasRespuestas = respuestaService.listarTodas(paginacion);
        return ResponseEntity.ok(todasLasRespuestas);
    }
}
