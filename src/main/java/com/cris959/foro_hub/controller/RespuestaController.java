package com.cris959.foro_hub.controller;

import com.cris959.foro_hub.dto.DatosRegistroRespuesta;
import com.cris959.foro_hub.dto.DatosRetornoRespuesta;
import com.cris959.foro_hub.service.IRespuestaService;
import com.cris959.foro_hub.service.IRespuestaServiceIA;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(
        name = "Respuestas del Foro",
        description = "CRUD de respuestas ligadas a tópicos. USER: crear/leer. ADMIN: marcar solución."
)
@RestController
@RequestMapping("/api/respuestas")
@SecurityRequirement(name = "bearerAuth")
public class RespuestaController {

    private final IRespuestaService respuestaService;

    private final IRespuestaServiceIA  respuestaServiceIA;

    public RespuestaController(IRespuestaService respuestaService, IRespuestaServiceIA respuestaServiceIA) {
        this.respuestaService = respuestaService;
        this.respuestaServiceIA = respuestaServiceIA;
    }

    @Operation(
            summary = "Crear respuesta con moderación de IA",
            description = """
                    Registra una respuesta para un tópico específico. 
                    El contenido es analizado automáticamente por un modelo de IA (Gemini) 
                    para detectar lenguaje ofensivo antes de ser guardado. 
                    Requiere roles: USER o ADMIN.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Respuesta creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o contenido bloqueado por política de moderación (IA)"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Tópico o Autor no encontrado")
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<DatosRetornoRespuesta> registrar(@RequestBody @Valid DatosRegistroRespuesta datos, UriComponentsBuilder uriComponentsBuilder) {
        DatosRetornoRespuesta respuesta = respuestaServiceIA.registrar(datos);

        // Creamos la URL dinámica para la nueva respuesta
        URI url = uriComponentsBuilder.path("/respuestas/{id}").buildAndExpand(respuesta.id()).toUri();
//        System.out.println("Entrando al procedimiento registrar respuesta...");
        return ResponseEntity.created(url).body(respuesta);
    }

    @Operation(
            summary = "Marcar como solución",
            description = "Marca respuesta como solución oficial del tópico. Solo ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Marcada como solución"),
            @ApiResponse(responseCode = "404", description = "Respuesta no encontrada")
    })
    @PutMapping("/{id}/solucion")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Transactional
    public ResponseEntity marcarComoSolucion(@PathVariable Long id) {
        respuestaService.marcarComoSolucion(id);
        // Aquí llamarías a un procedimiento del service que cambie el boolean 'solucion' a true
        return ResponseEntity.ok("La respuesta ha sido marcada como la solución definitiva!");
    }

    @Operation(
            summary = "Respuestas por tópico",
            description = "Lista paginada de respuestas de un tópico (sort=fechaCreacion ASC). Soporta ?page=0&size=5."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Página de respuestas")
    })
    @GetMapping("/respuesta/{topicoId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Page<DatosRetornoRespuesta>> listarPorTopico(@PathVariable Long topicoId,
                                                                       @PageableDefault(size = 10, sort = "fechaCreacion", direction = Sort.Direction.ASC) Pageable paginacion) {
        var pagina = respuestaService.listarPorTopico(topicoId, paginacion);
        return ResponseEntity.ok(pagina);
    }

    @Operation(
            summary = "Todas las respuestas",
            description = "Lista global paginada (sort=fechaCreacion DESC). USER/ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Página de todas las respuestas")
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Page<DatosRetornoRespuesta>> listarTodasLasRespuestas(
            @PageableDefault(size = 10, sort = "fechaCreacion", direction = Sort.Direction.DESC) Pageable paginacion) {
        var todasLasRespuestas = respuestaService.listarTodas(paginacion);
        return ResponseEntity.ok(todasLasRespuestas);
    }
}
