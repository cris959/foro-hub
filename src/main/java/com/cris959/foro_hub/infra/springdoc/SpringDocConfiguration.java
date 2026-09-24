package com.cris959.foro_hub.infra.springdoc;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfiguration {
    static {
        // Mantenemos el reemplazo de clase para que la entrada sea limpia
        org.springdoc.core.utils.SpringDocUtils.getConfig()
                .replaceWithClass(org.springframework.data.domain.Pageable.class,
                        org.springdoc.core.converters.models.Pageable.class);
    }
    @Bean
    public OpenAPI customOpenAPI() {
        // Definimos el nombre de la llave de seguridad una sola vez para evitar errores
        final String securitySchemeName = "bearer-key";

        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName) // Nombre del esquema
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                .info(new Info()
                        .title("Foro Hub API - Moderación Inteligente & Resiliencia AI")
                        .description("""
                API REST profesional para la gestión de tópicos y respuestas, protegida por una infraestructura de seguridad híbrida.
                
                **Capa de Seguridad y Moderación:**
                * **Nivel Principal:** Análisis semántico avanzado con **Google Gemini** para la detección de contenido ofensivo y validación de contexto.
                * **Nivel de Respaldo (Fallback AI):** En caso de exceder cuotas o latencia en el servicio principal, se activa automáticamente **Mistral AI** para garantizar la continuidad.
                * **Contingencia Local (Offline):** Filtro heurístico basado en reglas y puntuación de riesgo para protección contra Spam y lenguaje inapropiado si no hay conexión a servicios externos.
                
                **Seguridad de Acceso:**
                * **Protección JWT:** Autenticación robusta mediante tokens Bearer para asegurar que cada acción sea realizada por un usuario autorizado.
                """)
                        .version("1.2 (Multi-Model AI)")
                        .contact(new Contact()
                                .name("Equipo Backend-Cris959")
                                .email("backend@forohub.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://foro.hub/api/licencia")))
                // Aplicar el esquema de seguridad de forma global a todos los endpoints
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName));
    }
    /**
     * Consolidamos ambos customizers en uno solo.
     * Este Bean elimina cualquier rastro de los esquemas que generan números gigantes,
     * tanto para la entrada (Pageable) como para la salida (PagedModel/Metadata).
     */
    @Bean
    public org.springdoc.core.customizers.OpenApiCustomizer customerOpenApiCustomizer() {
        return openApi -> {
            var components = openApi.getComponents();
            if (components != null && components.getSchemas() != null) {
                var schemas = components.getSchemas();

                // 1. Limpieza de entrada (Query Parameters)
                schemas.remove("Pageable");
                schemas.remove("org.springframework.data.domain.Pageable");
                schemas.remove("org.springframework.data.domain.AbstractPageRequest");

                // 2. Limpieza de salida (Respuesta via-dto)
                schemas.remove("PageMetadata");
                schemas.remove("PagedModel");
                // Tambien quitamos la version generica de Page que a veces crea Mistral
                schemas.remove("Page");
            }
        };
    }
}