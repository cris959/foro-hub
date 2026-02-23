package com.cris959.foro_hub.infra.errores;

public record DatosErrorValidacion(
        String campo,
        String error
) {
}
