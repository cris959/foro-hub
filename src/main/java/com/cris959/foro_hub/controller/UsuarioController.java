package com.cris959.foro_hub.controller;

import com.cris959.foro_hub.dto.DatosRegistroUsuario;
import com.cris959.foro_hub.dto.DatosRespuestaUsuario;
import com.cris959.foro_hub.service.IUsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final IUsuarioService usuarioService;

    public UsuarioController(IUsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    public ResponseEntity<DatosRespuestaUsuario> registrar(@RequestBody @Valid DatosRegistroUsuario datos,
                                                           UriComponentsBuilder uriComponentsBuilder) {
        // Llamamos al servicio (que usa el mapper y el repositorio)
        DatosRespuestaUsuario respuesta = usuarioService.registrarUsuario(datos);

        // Buenas prácticas: Retornar 201 Created y la URL del nuevo recurso
        URI url = uriComponentsBuilder.path("/usuarios/{id}").buildAndExpand(respuesta.id()).toUri();
        return ResponseEntity.created(url).body(respuesta);
    }
}
