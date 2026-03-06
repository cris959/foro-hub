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
import com.cris959.foro_hub.dto.DatosActualizarUsuario;
import com.cris959.foro_hub.dto.DatosRegistroUsuario;
import com.cris959.foro_hub.dto.DatosRespuestaUsuario;
import com.cris959.foro_hub.service.IUsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;


@Tag(
        name = "Gestión de Usuarios",
        description = "API para CRUD completo de usuarios con roles de seguridad. ADMIN: todas las operaciones. USER: lectura limitada."
)
@RestController
@RequestMapping("/api/usuarios")
@SecurityRequirement(name = "bearerAuth")
public class UsuarioController {

    private final IUsuarioService usuarioService;

    public UsuarioController(IUsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }


    @Operation(
            summary = "Registrar nuevo usuario",
            description = "Crea un usuario nuevo. Solo ADMIN. Retorna 201 con URL del recurso."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos ADMIN")
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<DatosRespuestaUsuario> registrar(@RequestBody @Valid DatosRegistroUsuario datos,
                                                           UriComponentsBuilder uriComponentsBuilder) {
        // Llamamos al servicio (que usa el mapper y el repositorio)
        DatosRespuestaUsuario respuesta = usuarioService.registrarUsuario(datos);

        // Buenas prácticas: Retornar 201 Created y la URL del nuevo recurso
        URI url = uriComponentsBuilder.path("/usuarios/{id}").buildAndExpand(respuesta.id()).toUri();
        return ResponseEntity.created(url).body(respuesta);
    }


    @Operation(
            summary = "Detalle de usuario por ID",
            description = "Obtiene datos completos de un usuario por su ID. USER o ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<DatosRespuestaUsuario> detallarUsuario(@PathVariable Long id) {
        var usuario = usuarioService.obtenerPorId(id);
        return ResponseEntity.ok(usuario);
    }


    @Operation(
            summary = "Buscar usuario por email",
            description = "Busca usuario exacto por email. USER o ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/buscar")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<DatosRespuestaUsuario> buscarPorEmail(
            @Parameter(description = "Email del usuario", required = true, example = "user@example.com")
            @RequestParam String email) {
        var usuario = usuarioService.buscarPorEmail(email);
        return ResponseEntity.ok(usuario);
    }


    @Operation(
            summary = "Actualizar usuario",
            description = "Actualiza datos de usuario existente. Solo ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario actualizado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Transactional
    public ResponseEntity actualizar(@PathVariable Long id,
                                     @RequestBody @Valid DatosActualizarUsuario datos) {
        var usuarioActualizado = usuarioService.actualizar(id, datos);
        return ResponseEntity.ok(usuarioActualizado);
    }


    @Operation(
            summary = "Eliminar usuario",
            description = "Elimina usuario por ID. Solo ADMIN. Retorna 204."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuario eliminado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Transactional
    public ResponseEntity eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Activar usuario",
            description = "Activa usuario inactivo por ID. Solo ADMIN. Retorna 204."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuario activado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PostMapping("/{id}/activar")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Transactional
    public ResponseEntity activar(@PathVariable Long id) {
        usuarioService.activar(id);
        return ResponseEntity.noContent().build(); // Retornamos 204 porque la operación fue exitosa
    }

    @Operation(
            summary = "Listar usuarios inactivos",
            description = "Lista todos los usuarios inactivos. USER o ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de usuarios inactivos")
    })
    @GetMapping("/inactivos")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<DatosRespuestaUsuario>> listarInactivos() {
        var lista = usuarioService.listarInactivos();
        return ResponseEntity.ok(lista);
    }
}
