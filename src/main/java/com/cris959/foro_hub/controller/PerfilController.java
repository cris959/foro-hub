package com.cris959.foro_hub.controller;

import com.cris959.foro_hub.dto.DatosListaPerfil;
import com.cris959.foro_hub.dto.DatosRegistroPerfil;
import com.cris959.foro_hub.service.IPerfilService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/perfiles")
public class PerfilController {

    private final IPerfilService perfilService;

    public PerfilController(IPerfilService perfilService) {
        this.perfilService = perfilService;
    }

    @PostMapping
    public ResponseEntity<DatosListaPerfil> registrar(@RequestBody @Valid DatosRegistroPerfil datos) {
        var perfil = perfilService.guardar(datos);
        return ResponseEntity.ok(perfil);
    }

    @GetMapping
    public ResponseEntity<List<DatosListaPerfil>> listar() {
        var perfiles = perfilService.listarPerfiles();
        return ResponseEntity.ok(perfiles);
    }
}
