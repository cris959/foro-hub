package com.cris959.foro_hub.controller;

import com.cris959.foro_hub.dto.DatosRegistroRespuesta;
import com.cris959.foro_hub.dto.DatosRetornoRespuesta;
import com.cris959.foro_hub.service.IRespuestaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/respuestas")
public class RespuestaController {

    private final IRespuestaService respuestaService;

    public RespuestaController(IRespuestaService respuestaService) {
        this.respuestaService = respuestaService;
    }

    @PostMapping
    public ResponseEntity<DatosRetornoRespuesta> registrar(@RequestBody @Valid DatosRegistroRespuesta datos) {
        var respueta= respuestaService.registar(datos);
        return ResponseEntity.ok(respueta);
    }

    // Obtener respuestas de un tópico específico
    @GetMapping("/topico/{id}")
    public ResponseEntity<List<DatosRetornoRespuesta>> listarPorTopico(@PathVariable Long id) {
        return ResponseEntity.ok(respuestaService.listarPorTopico(id));
    }

    // Ejemplo de cómo marcar una respuesta como solución
    @PutMapping("/{id}/solucion")
    @Transactional
    public ResponseEntity marcarComoSolucion(@PathVariable Long id) {
        // Aquí llamarías a un método del service que cambie el boolean 'solucion' a true
        return ResponseEntity.noContent().build();
    }
}
