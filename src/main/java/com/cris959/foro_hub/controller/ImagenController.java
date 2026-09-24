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

import com.cris959.foro_hub.service.IUsuarioService;
import com.cris959.foro_hub.service.StorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/imagenes")
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Imágenes", description = "Endpoints para gestión de fotos de perfil")
public class ImagenController {


    private final StorageService storageService;
    private final IUsuarioService usuarioService;

    public ImagenController(StorageService storageService, IUsuarioService usuarioService) {
        this.storageService = storageService;
        this.usuarioService = usuarioService;
    }

    @Operation(summary = "Sube una foto de perfil", description = "Permite subir un archivo de imagen (jpg/png) para el usuario autenticado.")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> subirFotoPerfil(
            @Parameter(description = "Archivo de imagen a subir")
            @RequestParam("file") MultipartFile archivo) {
        // 1. Obtener el email del usuario desde el contexto de seguridad (JWT)
        var emailUsuario = SecurityContextHolder.getContext().getAuthentication().getName();

        // 2. Subir la imagen a Oracle Cloud y obtener el nombre unico
        String rutaImagen = storageService.subirImagen(archivo);

        // 3. Actualizar la base de datos para ese usuario especifico
        usuarioService.actualizarFotoPerfil(emailUsuario, rutaImagen);

        return ResponseEntity.ok("Foto actualizada para el usuario: " + emailUsuario);
    }

    @Operation(summary = "Visualiza una imagen", description = "Retorna el contenido binario de la imagen desde Oracle Cloud.")
    @GetMapping(value = "/ver/{nombre:.+}", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_GIF_VALUE})
    public ResponseEntity<byte[]> verImagen(@PathVariable String nombre) {
        // 1. Llamamos al procedimiento que ya corregimos en el StorageService
        byte[] imagenBytes = storageService.obtenerImagen(nombre);

        // 2. EL FRAGMENTO DE DETECCION: Colocalo aqui
        MediaType type = MediaType.IMAGE_JPEG; // Por defecto para .jpg y .jpeg

        String nombreMinusculas = nombre.toLowerCase();

        if (nombreMinusculas.endsWith(".png")) {
            type = MediaType.IMAGE_PNG;
        } else if (nombreMinusculas.endsWith(".gif")) {
            type = MediaType.IMAGE_GIF;
        }

        // 3. Retornamos la respuesta con el tipo de contenido dinamico
        return ResponseEntity.ok()
                .contentType(type)
                // Obliga al navegador a preguntar al servidor cada vez (o tras X segundos)
                .header(HttpHeaders.CACHE_CONTROL, "no-cache, max-age=0")
                .body(imagenBytes);
    }
}