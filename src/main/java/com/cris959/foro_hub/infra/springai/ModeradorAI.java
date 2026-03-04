package com.cris959.foro_hub.infra.springai;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.stereotype.Component;

@Component
public class ModeradorAI {

    private final ChatModel chatModel;

    public ModeradorAI(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public boolean esContenidoOfensivo(String mensaje) {
        // Definimos el rol de Gemini
        String systemText = """
                Eres un moderador estricto.\s
                Analiza el mensaje y responde ÚNICAMENTE con la palabra 'OFENSIVO' si contiene\s
                insultos, faltas de respeto o lenguaje de odio.\s
                De lo contrario responde 'SEGURO'.\s
                No añadas explicaciones ni puntuación.
                """;

        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemText);
        var systemMessage = systemPromptTemplate.createMessage();
        var userMessage = "Analiza este comentario: " + mensaje;

        var response = chatModel.call(new Prompt(java.util.List.of(systemMessage,
                new org.springframework.ai.chat.messages.UserMessage(userMessage))));

        String resultado = response.getResult().getOutput().getText().trim().toUpperCase();

        // ESTO ES CLAVE: Mira qué está respondiendo la IA realmente
//        System.out.println("VEREDICTO DE LA IA: [" + resultado + "]");

        return resultado.toUpperCase().contains("OFENSIVO");
    }
}
