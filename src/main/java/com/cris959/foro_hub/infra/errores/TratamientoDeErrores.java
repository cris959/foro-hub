package com.cris959.foro_hub.infra.errores;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class TratamientoDeErrores {

    // 1. Manejar error 404 cuando no se encuentra un ID (findById().orElseThrow())
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Void> tratarError404() {
        return ResponseEntity.notFound().build();
    }

    // 2. Manejar errores de validación de los campos (@Valid) - Error 400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<DatosErrorValidacion>> tratarError400(MethodArgumentNotValidException ex) {
        var errores = ex.getFieldErrors().stream()
                .map(DatosErrorValidacion::new)
                .toList();
        return ResponseEntity.badRequest().body(errores);
    }

    // 3. Manejar errores de lógica de negocio (Validaciones personalizadas)
    @ExceptionHandler(ValidacionException.class) // Si creas una clase propia de excepción
    public ResponseEntity<String> tratarErrorDeValidacionDeNegocio(ValidacionException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    // DTO para estructurar el error de validación de campos
    private record DatosErrorValidacion(String campo, String error) {
        public DatosErrorValidacion(FieldError error) {
            this(error.getField(), error.getDefaultMessage());
        }
    }
}
