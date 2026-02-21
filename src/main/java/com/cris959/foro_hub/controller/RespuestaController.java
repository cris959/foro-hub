package com.cris959.foro_hub.controller;

import com.cris959.foro_hub.dto.DatosRegistroRespuesta;
import com.cris959.foro_hub.dto.DatosRetornoRespuesta;
import com.cris959.foro_hub.service.IRespuestaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/respuestas")
public class RespuestaController {

    private final IRespuestaService respuestaService;

    public RespuestaController(IRespuestaService respuestaService) {
        this.respuestaService = respuestaService;
    }

    @PostMapping
    public ResponseEntity<DatosRetornoRespuesta> registrar(@RequestBody @Valid DatosRegistroRespuesta datos, UriComponentsBuilder uriComponentsBuilder) {
        DatosRetornoRespuesta respuesta = respuestaService.registrar(datos);

        // Creamos la URL dinámica para la nueva respuesta
        URI url = uriComponentsBuilder.path("/respuestas/{id}").buildAndExpand(respuesta.id()).toUri();

        return ResponseEntity.created(url).body(respuesta);
    }

    // Obtener respuestas de un tópico específico
    @GetMapping("/topico/{idTopico}")
    public ResponseEntity<Page<DatosRetornoRespuesta>> listarPorTopico(@PathVariable Long idTopico,
                                                                       @PageableDefault(size = 10, sort = "fechaCreacion")Pageable paginacion) {
        var respuestas = respuestaService.listarPorTopico(idTopico, paginacion);
        return ResponseEntity.ok(respuestas);
    }

    // Ejemplo de cómo marcar una respuesta como solución
    @PutMapping("/{id}/solucion")
    @Transactional
    public ResponseEntity marcarComoSolucion(@PathVariable Long id) {
        // Aquí llamarías a un método del service que cambie el boolean 'solucion' a true
        return ResponseEntity.noContent().build();
    }
}
