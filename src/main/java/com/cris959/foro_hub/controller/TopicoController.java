package com.cris959.foro_hub.controller;

import com.cris959.foro_hub.dto.DatosActualizarTopico;
import com.cris959.foro_hub.dto.DatosRegistroTopico;
import com.cris959.foro_hub.dto.DatosRespuestaTopico;
import com.cris959.foro_hub.service.ITopicoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Tag(
        name = "Tópicos del Foro",
        description = "Gestión CRUD de tópicos con paginación, activación y roles. USER: crear/leer. ADMIN: todo."
)
@RestController
@RequestMapping("/api/topicos")
@SecurityRequirement(name = "bearerAuth")
public class TopicoController {

    private final ITopicoService topicoService;

    public TopicoController(ITopicoService topicoService) {
        this.topicoService = topicoService;
    }

    // POST: Crear un nuevo tópico
    @Operation(
            summary = "Registra un nuevo tópico",
            description = "Crea un tópico vinculado a un usuario y un curso específicos.",
            tags = { "tópicos" }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tópico creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<DatosRespuestaTopico> registrar(@RequestBody @Valid DatosRegistroTopico datos, UriComponentsBuilder uriBuilder) {
        var topicoResponse = topicoService.crear(datos);
        var uri = uriBuilder.path("/topicos/{id}").buildAndExpand(topicoResponse.id()).toUri();
        return ResponseEntity.created(uri).body(topicoResponse);
    }

    // GET: Listar tópicos con paginación
    @Operation(
            summary = "Listar tópicos paginados",
            description = "Lista con paginación por defecto (size=10, sort=fechaCreacion). Soporta ?page=0&size=20&sort=titulo,desc. USER/ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Página de tópicos")
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Page<DatosRespuestaTopico>> listar(@PageableDefault(size = 10, sort = {"fechaCreacion"}) Pageable paginacion) {
        return ResponseEntity.ok(topicoService.listar(paginacion));
    }

    // GET: Buscar un tópico por ID
    @Operation(
            summary = "Detalle tópico por ID",
            description = "Obtiene tópico completo por ID. USER/ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tópico encontrado"),
            @ApiResponse(responseCode = "404", description = "No encontrado")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<DatosRespuestaTopico> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(topicoService.buscarPorId(id));
    }

    // PUT: Actualizar un tópico
    @Operation(
            summary = "Actualizar tópico",
            description = "Modifica título/mensaje/estado por ID. Solo ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tópico actualizado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "No encontrado")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Transactional
    public ResponseEntity<DatosRespuestaTopico> actualizar(@PathVariable Long id,
                                                           @RequestBody @Valid DatosActualizarTopico datos) {
        var respuesta = topicoService.actualizar(id, datos);
        return ResponseEntity.ok(respuesta);
    }

    // DELETE: Eliminar un tópico
    @Operation(
            summary = "Eliminar tópico",
            description = "Borra tópico por ID. Solo ADMIN. Retorna 204."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Eliminado"),
            @ApiResponse(responseCode = "404", description = "No encontrado")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Transactional
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        topicoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Historial completo de tópicos",
            description = "Lista todos (activos/inactivos) sin paginación. USER/ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista completa")
    })
    @GetMapping("/historial")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<DatosRespuestaTopico>> listarActivoEInactivos() {
        return ResponseEntity.ok(topicoService.listarTodoElHistorial());
    }

    @Operation(
            summary = "Activar tópico",
            description = "Reactiva tópico inactivo por ID. Solo ADMIN. Retorna 204."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Activado"),
            @ApiResponse(responseCode = "404", description = "No encontrado")
    })
    @PostMapping("/{id}/activar")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Transactional
    public ResponseEntity activar(@PathVariable Long id) {
        topicoService.activar(id);
        return ResponseEntity.noContent().build();
    }
}
