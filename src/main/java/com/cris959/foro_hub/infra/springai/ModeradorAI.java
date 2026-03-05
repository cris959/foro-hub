package com.cris959.foro_hub.infra.springai;

import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ModeradorAI {

    private final ChatModel chatModel;

    public ModeradorAI(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public boolean esContenidoOfensivo(String mensaje, String autor) {
        // Definimos el rol de Gemini
        String systemText = """
                Eres un moderador experto y estricto.
                 Analiza el mensaje y el nombre del autor.
                 Responde ÚNICAMENTE con la palabra 'OFENSIVO' si detectas cualquiera de estos puntos:
                1. INSULTOS: Lenguaje de odio, faltas de respeto o groserías.
                2. SPAM: Enlaces (http/www), ofertas de dinero rápido, o texto repetitivo sin sentido.
                3. AUTOR SOSPECHOSO: Si el nombre del autor intenta suplantar identidad (admin, support, system) o parece un bot.
                 De lo contrario, responde 'SEGURO'.
                 No añadas explicaciones ni puntos finales.
                """;

        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemText);
        var systemMessage = systemPromptTemplate.createMessage();
        var userMessage = String.format("Autor: %s | Mensaje: %s", autor, mensaje);

        var response = chatModel.call(new Prompt(java.util.List.of(systemMessage,
                new org.springframework.ai.chat.messages.UserMessage(userMessage))));

        String resultado = response.getResult().getOutput().getText().trim().toUpperCase();

        // ESTO ES CLAVE: Mira qué está respondiendo la IA realmente
//        System.out.println("VEREDICTO DE LA IA: [" + resultado + "]");

        return resultado.toUpperCase().contains("OFENSIVO");
    }

    public String generarResumenEstadistico(List<String> titulosTopicos) {
        String fechaActual = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        String systemText = """
                Eres un analista de datos experto en comunidades tecnológicas.
                Te proporcionaré una lista de títulos de tópicos de un foro.
                Tu tarea es identificar los 3 temas más recurrentes y dar un breve consejo 
                sobre qué contenido o curso debería crear el foro para ayudar a los usuarios.
                RESPONDE SIEMPRE SOLO EN FORMATO JSON VÁLIDO con esta estructura exacta:
                        {
                          "popularidad": ["tema1", "tema2", "tema3"],
                          "sugerencia": "Tu sugerencia aquí",
                          "fuente": "IA_ANALISIS",
                          "fecha_analisis": "dd/MM/yyyy HH:mm"
                        }
                
                        - 'popularidad': máximo 3 temas (array de strings)
                        - 'sugerencia': máximo 150 caracteres
                        - Incluye fecha actual en formato dd/MM/yyyy HH:mm
                        - NUNCA agregues texto fuera del JSON
                """.formatted(fechaActual);

        String userText = "Títulos: " + String.join(", ", titulosTopicos);

        var response = chatModel.call(new Prompt(List.of(
                new SystemMessage(systemText),
                new UserMessage(userText)
        )));

        return response.getResult().getOutput().getText();
    }
}
