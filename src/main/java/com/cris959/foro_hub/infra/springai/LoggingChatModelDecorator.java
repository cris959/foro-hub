package com.cris959.foro_hub.infra.springai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;



public class LoggingChatModelDecorator implements ChatModel {

    private static final Logger log = LoggerFactory.getLogger("GEMINI-MONITOR");
    private final ChatModel delegate;


    public LoggingChatModelDecorator(ChatModel delegate) {
        this.delegate = delegate;
    }

    @Override
    public ChatResponse call(Prompt prompt) {
//        System.out.println(">>>> [LLAMADA A GEMINI DETECTADA POR EL DECORADOR] <<<<"); // Si no ves esto, el decorador no está conectado
        // Ejecutamos la llamada original
        ChatResponse response = delegate.call(prompt);

        // Extraemos y loqueamos el uso
        Usage usage = response.getMetadata().getUsage();

        log.info(">>>> [GEMINI USAGE REPORT] <<<<");
        log.info("Modelo: {}", response.getMetadata().getModel());
        log.info("Tokens Prompt: {}", usage.getPromptTokens());
        log.info("Tokens Generación: {}", usage.getCompletionTokens());
        log.info("Tokens Totales: {}", usage.getTotalTokens());
        log.info("-------------------------------");

        return response;
    }
}
