package com.cris959.foro_hub.controller;

import com.cris959.foro_hub.dto.DatosActualizarUsuario;
import com.cris959.foro_hub.dto.DatosRegistroUsuario;
import com.cris959.foro_hub.dto.DatosRespuestaUsuario;
import com.cris959.foro_hub.service.IUsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
//@Validated // Esto permite validar @RequestParam y @PathVariable
public class UsuarioController {

    private final IUsuarioService usuarioService;

    public UsuarioController(IUsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

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

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<DatosRespuestaUsuario> detallarUsuario(@PathVariable Long id) {
        var usuario = usuarioService.obtenerPorId(id);
        return ResponseEntity.ok(usuario);
    }

    @GetMapping("/buscar")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<DatosRespuestaUsuario> buscarPorEmail(@RequestParam String email) {
        var usuario = usuarioService.buscarPorEmail(email);
        return ResponseEntity.ok(usuario);
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Transactional
    public ResponseEntity actualizar(@RequestBody @Valid DatosActualizarUsuario datos) {
        var usuarioActualizado = usuarioService.actualizar(datos);
        return ResponseEntity.ok(usuarioActualizado);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Transactional
    public ResponseEntity eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/activar")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Transactional
    public ResponseEntity activar(@PathVariable Long id) {
        usuarioService.activar(id);
        return ResponseEntity.noContent().build(); // Retornamos 204 porque la operación fue exitosa
    }

    @GetMapping("/inactivos")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<DatosRespuestaUsuario>> listarInactivos() {
        var lista = usuarioService.listarInactivos();
        return ResponseEntity.ok(lista);
    }
}
