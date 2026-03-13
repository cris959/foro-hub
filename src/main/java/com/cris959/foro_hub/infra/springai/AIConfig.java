package com.cris959.foro_hub.infra.springai;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.mistralai.MistralAiChatOptions;
import org.springframework.ai.mistralai.api.MistralAiApi;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AIConfig {

    @Bean
    @Primary
    public ChatModel foroHubChatModel(@Qualifier("googleGenAiChatModel") ChatModel googleGemini,
                                      @Qualifier("mistralChatModel") ChatModel mistral) {
        return new LoggingChatModelDecorator(googleGemini);
    }

    @Bean
    @Qualifier("mistralChatModel")
    public ChatModel mistralChatModel(@Value("${spring.ai.mistralai.api-key}") String apiKey) {

        // 1. Infraestructura base
        var restClientBuilder = org.springframework.web.client.RestClient.builder();
        var webClientBuilder = org.springframework.web.reactive.function.client.WebClient.builder();

        // 2. Cliente API (Sin el /v1 al final para evitar el error 404 anterior)
        MistralAiApi mistralAiApi = new MistralAiApi(
                "https://api.mistral.ai",
                apiKey,
                restClientBuilder,
                webClientBuilder,
                new org.springframework.web.client.DefaultResponseErrorHandler()
        );

        // 3. Opciones del modelo
        MistralAiChatOptions options = MistralAiChatOptions.builder()
                .model("mistral-small-latest")
                .temperature(0.1)
                .internalToolExecutionEnabled(false)
                .build();
        // Manager vacío (pero no nulo)
        var toolCallingManager = org.springframework.ai.model.tool.ToolCallingManager.builder().build();

        // Registro de observaciones local
        var localRegistry = io.micrometer.observation.ObservationRegistry.create();

        // Predicado en false para mayor seguridad
        org.springframework.ai.model.tool.ToolExecutionEligibilityPredicate predicate = (prompt, metadata) -> false;

        // --- EL CONSTRUCTOR DE 6 PARÁMETROS ---
        return new org.springframework.ai.mistralai.MistralAiChatModel(
                mistralAiApi,                                         // 1. Api
                options,                                              // 2. Options
                toolCallingManager,                                   // 3. Tool Manager
                new org.springframework.retry.support.RetryTemplate(), // 4. Retry
                localRegistry,                                        // 5. Observation
                predicate                                             // 6. Predicate
        );
    }
}