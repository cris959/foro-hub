package com.cris959.foro_hub.infra.errores;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

@RestControllerAdvice
public class TratamientoDeErrores {

    // 1. Manejar error 404 cuando no se encuentra un ID (findById().orElseThrow())
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity tratarError404(EntityNotFoundException e) {
/// Usamos "Recurso" o "ID" como campo genérico para que sirva para cualquier entidad
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new DatosErrorValidacion("error", e.getMessage()));

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

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity tratarErrorConversionParametro(MethodArgumentTypeMismatchException e) {
        // Verificamos si el error es específicamente por un Enum
        if (e.getRequiredType() != null && e.getRequiredType().isEnum()) {
            String mensaje = String.format("El valor '%s' no es un perfil válido. Los valores permitidos son: %s",
                    e.getValue(),
                    java.util.Arrays.toString(e.getRequiredType().getEnumConstants()));

            return ResponseEntity.badRequest().body(new DatosErrorValidacion(e.getName(), mensaje));
        }

        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity tratarErrorDuplicados() {
        return ResponseEntity.badRequest().body(new DatosErrorValidacion("base de datos", "Ya existe un registro con esos datos (valor duplicado)."));
    }

    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity tratarError400CuerpoVacio(org.springframework.http.converter.HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest().body(new DatosErrorValidacion("cuerpo", "El cuerpo de la solicitud (JSON) no puede estar vacío."));
    }
}
