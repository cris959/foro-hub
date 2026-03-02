package com.cris959.foro_hub.infra.exception;

public record DatosErrorValidacion(
        String campo,
        String error
) {
}
