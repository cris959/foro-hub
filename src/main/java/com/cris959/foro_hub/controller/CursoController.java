package com.cris959.foro_hub.controller;

import com.cris959.foro_hub.dto.DatosRegistroCurso;
import com.cris959.foro_hub.dto.DatosRespuestaCurso;
import com.cris959.foro_hub.service.ICursoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@Tag(
        name = "Gestión de Cursos",
        description = "CRUD de cursos del foro. ADMIN: crear/eliminar. USER/ADMIN: leer con paginación."
)
@RestController
@RequestMapping("/api/cursos")
@SecurityRequirement(name = "bearerAuth")
public class CursoController {

    private final ICursoService cursoService;

    public CursoController(ICursoService cursoService) {
        this.cursoService = cursoService;
    }

    // POST: Crear un nuevo curso
    @Operation(
            summary = "Crear nuevo curso",
            description = "Registra curso nuevo. Solo ADMIN. Retorna 201 con URL del recurso."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Curso creado", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<DatosRespuestaCurso> registrar(@RequestBody @Valid DatosRegistroCurso datos, UriComponentsBuilder uriBuilder) {
        DatosRespuestaCurso cursoResponse = cursoService.registrar(datos);
        var uri = uriBuilder.path("/cursos/{id}").buildAndExpand(cursoResponse.id()).toUri();
        return ResponseEntity.created(uri).body(cursoResponse);
    }

    // GET: Listar cursos activos con paginación
    @Operation(
            summary = "Listar cursos activos",
            description = "Paginación por defecto (size=10, sort=nombre ASC). Soporta ?page=0&size=20&sort=nombre,desc. USER/ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Página de cursos activos")
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Page<DatosRespuestaCurso>> listar(@PageableDefault(size = 10, sort = {"nombre"}) Pageable paginacion) {
        return ResponseEntity.ok(cursoService.listar(paginacion));
    }

    // GET: Buscar curso por ID
    @Operation(
            summary = "Detalle curso por ID",
            description = "Obtiene curso completo por ID. USER/ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Curso encontrado"),
            @ApiResponse(responseCode = "404", description = "Curso no encontrado")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<DatosRespuestaCurso> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(cursoService.buscarPorId(id));
    }

    // DELETE: Borrado lógico de un curso
    @Operation(
            summary = "Eliminar curso (lógico)",
            description = "Borrado lógico (soft delete). Solo ADMIN. Retorna 204."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Curso eliminado lógicamente"),
            @ApiResponse(responseCode = "404", description = "Curso no encontrado")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        cursoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
