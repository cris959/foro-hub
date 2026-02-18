package com.cris959.foro_hub.infra.errores;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class TratamientoDeErrores {

    // 1. Manejar errores de validación de los campos (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<DatosErrorValidacion>> tratarErrorValidacion(MethodArgumentNotValidException ex) {
        var errores = ex.getFieldErrors().stream()
                .map(DatosErrorValidacion::new)
                .toList();
        return ResponseEntity.badRequest().body(errores);
    }

    // 2. Manejar errores de negocio personalizados (como el duplicado)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> tratarErrorNegocio(RuntimeException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    // DTO para estructurar el error de validación de campos
    private record DatosErrorValidacion(String campo, String error) {
        public DatosErrorValidacion(FieldError error) {
            this(error.getField(), error.getDefaultMessage());
        }
    }
}
