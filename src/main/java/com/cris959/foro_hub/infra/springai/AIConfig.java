package com.cris959.foro_hub.infra.springai;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AIConfig {

    @Bean
    @Primary
    public ChatModel chatModelWithLogging(ChatModel chatModel) {
//        System.out.println(">>> CONFIGURANDO DECORADOR DE IA <<<"); // Agrega esto para estar seguro
        return new LoggingChatModelDecorator(chatModel);
    }
}
