package com.cris959.foro_hub.infra.springai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;


public class LoggingChatModelDecorator implements ChatModel {

    private static final Logger log = LoggerFactory.getLogger("GEMINI-MONITOR");
    private final ChatModel delegate;

    public LoggingChatModelDecorator(ChatModel delegate){
        this.delegate = delegate;
    }

    @Override
    public ChatResponse call(Prompt prompt) {
        // Ejecutamos la llamada original
        ChatResponse response = delegate.call(prompt);

        // Extraemos y pintamos los logs
        logUsage(response);

        return response;
    }

    private void logUsage(ChatResponse response) {
        org.springframework.ai.chat.metadata.Usage usage = response.getMetadata().getUsage();

        // 2. Intentamos obtener el modelo desde los metadatos de la respuesta global
        String model = "gemini-3-flash-preview";
        if (response.getMetadata().containsKey("model")) {
            model = response.getMetadata().get("model").toString();
        }

        System.out.println("GEMINI-MONITOR             : >>>> [GEMINI USAGE REPORT] <<<<");
        System.out.println("GEMINI-MONITOR             : Modelo: " + model);
        System.out.println("GEMINI-MONITOR             : Tokens Prompt: " + (usage != null ? usage.getPromptTokens() : 0));
        System.out.println("GEMINI-MONITOR             : Tokens Generación: " + (usage != null ? usage.getCompletionTokens() : 0));
        System.out.println("GEMINI-MONITOR             : Tokens Totales: " + (usage != null ? usage.getTotalTokens() : 0));
        System.out.println("GEMINI-MONITOR             : ------------------------------");
    }
}
