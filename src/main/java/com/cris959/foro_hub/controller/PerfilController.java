package com.cris959.foro_hub.controller;

import com.cris959.foro_hub.dto.DatosListaPerfil;
import com.cris959.foro_hub.dto.DatosRegistroPerfil;
import com.cris959.foro_hub.model.PerfilNombre;
import com.cris959.foro_hub.service.IPerfilService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Tag(
        name = "Perfiles de Usuario",
        description = "Gestión de perfiles/roles de usuario. ADMIN: crear. USER/ADMIN: leer."
)
@RestController
@RequestMapping("/api/perfiles")
@SecurityRequirement(name = "bearerAuth")
public class PerfilController {

    private final IPerfilService perfilService;

    public PerfilController(IPerfilService perfilService) {
        this.perfilService = perfilService;
    }


    @Operation(
            summary = "Crear nuevo perfil",
            description = "Registra nuevo perfil de usuario. Solo ADMIN. Retorna 201 con URL."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Perfil creado", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<DatosListaPerfil> registrar(@RequestBody @Valid DatosRegistroPerfil datos,
                                                      UriComponentsBuilder uriComponentsBuilder) {
        // 1. Guardamos el perfil a través del service
        var perfil = perfilService.guardar(datos);

        // 2. Creamos la URL dinámica (ej: /perfiles/1)
        URI url = uriComponentsBuilder.path("/perfiles/{id}")
                .buildAndExpand(perfil.id()) // Asegúrate que DatosListaPerfil tenga el campo id
                .toUri();

        // 3. Retornamos 201 Created con la URL en el header y el cuerpo
        return ResponseEntity.created(url).body(perfil);
    }

    @Operation(
            summary = "Listar todos los perfiles",
            description = "Obtiene lista completa de perfiles disponibles. USER/ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de perfiles")
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<DatosListaPerfil>> listar() {
        var perfiles = perfilService.listarPerfiles();
        return ResponseEntity.ok(perfiles);
    }

    @Operation(
            summary = "Buscar perfil por nombre",
            description = "Busca perfil por su nombre (enum PerfilNombre). USER/ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil encontrado"),
            @ApiResponse(responseCode = "404", description = "Perfil no encontrado")
    })
    @GetMapping("/nombre/{nombre}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<DatosListaPerfil> buscarPorNombre(@PathVariable PerfilNombre nombre) {
        // Spring convertirá automáticamente el String de la URL al Enum PerfilNombre
        var perfil = perfilService.buscarPorNombre(nombre);
        return ResponseEntity.ok(perfil);
    }
}
