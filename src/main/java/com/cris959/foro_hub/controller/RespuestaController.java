package com.cris959.foro_hub.controller;
/*
Copyright 2026 Christian Garay

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
import com.cris959.foro_hub.dto.DatosRegistroRespuesta;
import com.cris959.foro_hub.dto.DatosRetornoRespuesta;
import com.cris959.foro_hub.service.IRespuestaService;
import com.cris959.foro_hub.service.moderacion.IRespuestaModeradorIA;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
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
@SecurityRequirement(name = "bearer-key")
public class RespuestaController {

    private final IRespuestaService respuestaService;

    private final IRespuestaModeradorIA respuestaServiceIA;

    public RespuestaController(IRespuestaService respuestaService, IRespuestaModeradorIA respuestaServiceIA) {
        this.respuestaService = respuestaService;
        this.respuestaServiceIA = respuestaServiceIA;
    }

    @Operation(
            summary = "Crear respuesta con moderación inteligente híbrida",
            description = """
        Registra una respuesta para un tópico específico con **doble sistema de moderación**:
        
        **Sistema híbrido de seguridad:**
        • **Primario**: IA (Gemini) analiza lenguaje ofensivo en tiempo real
        • **Backup**: Validaciones heurísticas locales si IA no responde
        
        **Flujo completo:**
        1. Validación DTO → 2. Moderación IA → 3. Guardado en BD → 4. Respuesta 201
        
        **Bloquea automáticamente:**
        - Insultos, amenazas, contenido explícito
        - SPAM detectado por IA
        - `fuente_moderacion` en respuesta indica: "IA_GEMINI" | "BACKUP_LOCAL"
        
        Requiere: **USER** o **ADMIN**
        """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201",
                    description = "Respuesta creada exitosamente tras pasar moderación IA",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = DatosRetornoRespuesta.class))),
            @ApiResponse(responseCode = "400",
                    description = "Datos inválidos O contenido bloqueado por IA/backup"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "Tópico o autor no encontrado")
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<DatosRetornoRespuesta> registrar(@RequestBody @Valid DatosRegistroRespuesta datos, UriComponentsBuilder uriComponentsBuilder) {
        DatosRetornoRespuesta respuesta = respuestaServiceIA.registrar(datos);

        // Creamos la URL dinamica para la nueva respuesta
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
    public ResponseEntity<String> marcarComoSolucion(@PathVariable Long id) {
        respuestaService.marcarComoSolucion(id);
        // Aqui llamarias a un procedimiento del service que cambie el boolean 'solucion' a true
        return ResponseEntity.ok("La respuesta ha sido marcada como la solución definitiva!");
    }

    @Operation(
            summary = "Listar respuestas por tópico",
            description = "Obtiene una lista paginada de todas las respuestas asociadas a un tópico específico. " +
                    "Por defecto, muestra 10 elementos por página ordenados por fecha de creación descendente."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Operación exitosa",
                    content = @Content(
                            mediaType = "application/json",
                            // Obligamos a Swagger a usar tu DTO para que no invente números
                            schema = @Schema(implementation = DatosRetornoRespuesta.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Tópico no encontrado")
    })
    @GetMapping("/topico/{topicoId}") // Cambie "respuesta" por "topico" para que sea mas semantico
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Page<DatosRetornoRespuesta>> listarPorTopico(
            @PathVariable Long topicoId,
            @ParameterObject //
            @PageableDefault(
                    size = 10,
                    page = 0,
                    sort = "fechaCreacion",
                    direction = Sort.Direction.DESC
            ) Pageable paginacion) {

        var pagina = respuestaService.listarPorTopico(topicoId, paginacion);
        return ResponseEntity.ok(pagina);
    }

    @Operation(
            summary = "Todas las respuestas",
            description = "Lista global paginada ordenada por fecha de creación descendente. Acceso para USER y ADMIN."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Pagina de todas las respuestas obtenida correctamente",
                    content = @Content(
                            mediaType = "application/json",
                            // Forzamos el uso de tu DTO para que la IA no invente números gigantes en la respuesta
                            schema = @Schema(implementation = DatosRetornoRespuesta.class)
                    )
            )
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Page<DatosRetornoRespuesta>> listarTodasLasRespuestas(
            @ParameterObject //
            @PageableDefault(
                    size = 10,
                    page = 0,
                    sort = "fechaCreacion",
                    direction = Sort.Direction.DESC
            ) Pageable paginacion) {

        // Recupera la lista paginada desde el servicio
        var todasLasRespuestas = respuestaService.listarTodas(paginacion);

        // Retorna la respuesta con estado 200 OK
        return ResponseEntity.ok(todasLasRespuestas);
    }

    @Operation(
            summary = "Elimina lógicamente una respuesta por ID",
            description = "Marca la respuesta como eliminada (soft delete), sin removerla físicamente de la BD. Requiere rol ADMIN"
    )
    @ApiResponse(responseCode = "204", description = "Respuesta marcada como eliminada exitosamente")
    @ApiResponse(responseCode = "403", description = "Acceso denegado - Requiere rol ADMIN")
    @ApiResponse(responseCode = "404", description = "Respuesta no encontrada")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Transactional
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        respuestaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}