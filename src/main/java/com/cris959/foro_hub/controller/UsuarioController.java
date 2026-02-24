package com.cris959.foro_hub.controller;

import com.cris959.foro_hub.dto.DatosActualizarUsuario;
import com.cris959.foro_hub.dto.DatosRegistroUsuario;
import com.cris959.foro_hub.dto.DatosRespuestaUsuario;
import com.cris959.foro_hub.service.IUsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/usuarios")
//@Validated // Esto permite validar @RequestParam y @PathVariable
public class UsuarioController {

    private final IUsuarioService usuarioService;

    public UsuarioController(IUsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<DatosRespuestaUsuario> registrar(@RequestBody @Valid DatosRegistroUsuario datos,
                                                           UriComponentsBuilder uriComponentsBuilder) {
        // Llamamos al servicio (que usa el mapper y el repositorio)
        DatosRespuestaUsuario respuesta = usuarioService.registrarUsuario(datos);

        // Buenas prácticas: Retornar 201 Created y la URL del nuevo recurso
        URI url = uriComponentsBuilder.path("/usuarios/{id}").buildAndExpand(respuesta.id()).toUri();
        return ResponseEntity.created(url).body(respuesta);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DatosRespuestaUsuario> detallarUsuario(@PathVariable Long id) {
        var usuario = usuarioService.obtenerPorId(id);
        return ResponseEntity.ok(usuario);
    }

    @GetMapping("/buscar")
    public ResponseEntity<DatosRespuestaUsuario> buscarPorEmail(@RequestParam String email) {
        var usuario = usuarioService.buscarPorEmail(email);
        return ResponseEntity.ok(usuario);
    }

    @PutMapping
    @Transactional
    public ResponseEntity actualizar(@RequestBody @Valid DatosActualizarUsuario datos) {
        var usuarioActualizado = usuarioService.actualizar(datos);
        return ResponseEntity.ok(usuarioActualizado);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/activar")
    @Transactional
    public ResponseEntity activar(@PathVariable Long id) {
        usuarioService.activar(id);
        return ResponseEntity.noContent().build(); // Retornamos 204 porque la operación fue exitosa
    }

    @GetMapping("/inactivos")
    public ResponseEntity<List<DatosRespuestaUsuario>> listarInactivos() {
        var lista = usuarioService.listarInactivos();
        return ResponseEntity.ok(lista);
    }
}
