package com.cris959.foro_hub.infra.springdoc;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfiguration {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearer-key",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                .info(new Info()
                        .title("Foro Hub API- Moderación Inteligente")
                        .description("""
                           API REST para la gestión de tópicos y respuestas del Foro Hub.
                            Características de Seguridad Avanzada:
                           * Moderación AI: Integración con Google Gemini para análisis semántico de contenido ofensivo,
                            spam y suplantación de identidad.
                           * Sistema de Respaldo (Fallback): En caso de indisponibilidad de la IA, la API activa
                            automáticamente un filtro heurístico local basado en puntuación de riesgo 
                            (Score-based Moderation).
                           * Protección JWT: Acceso seguro mediante tokens Bearer.
                        """)

                        .version("1.1 (AI-Enhanced)")
                        .contact(new Contact()
                                .name("Equipo Backend-Cris959")
                                .email("backend@forohub.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://foro.hub/api/licencia")))
                .addSecurityItem(new SecurityRequirement().addList("bearer-key"));
    }
}
