package com.cris959.foro_hub.controller;

import com.cris959.foro_hub.dto.DatosListaPerfil;
import com.cris959.foro_hub.dto.DatosRegistroPerfil;
import com.cris959.foro_hub.model.PerfilNombre;
import com.cris959.foro_hub.service.IPerfilService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/perfiles")
public class PerfilController {

    private final IPerfilService perfilService;

    public PerfilController(IPerfilService perfilService) {
        this.perfilService = perfilService;
    }

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

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<DatosListaPerfil>> listar() {
        var perfiles = perfilService.listarPerfiles();
        return ResponseEntity.ok(perfiles);
    }

    @GetMapping("/nombre/{nombre}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<DatosListaPerfil> buscarPorNombre(@PathVariable PerfilNombre nombre) {
        // Spring convertirá automáticamente el String de la URL al Enum PerfilNombre
        var perfil = perfilService.buscarPorNombre(nombre);
        return ResponseEntity.ok(perfil);
    }
}
