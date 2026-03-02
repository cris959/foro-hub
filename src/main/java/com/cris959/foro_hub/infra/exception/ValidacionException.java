package com.cris959.foro_hub.infra.exception;

public class ValidacionException extends RuntimeException{
    public ValidacionException(String mensaje) {
        super(mensaje);
    }
}
