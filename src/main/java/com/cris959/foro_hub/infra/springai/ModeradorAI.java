package com.cris959.foro_hub.infra.springai;

import com.cris959.foro_hub.dto.ResultadoModeracion;
import com.cris959.foro_hub.service.heuristica.AnalizadorHeuristicoTendencias;
import com.cris959.foro_hub.service.heuristica.ModeradorHeuristicoLocal;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class ModeradorAI {

    private final ChatModel geminiChatModel;

    private final ChatModel mistralChatModel;

    private final ModeradorHeuristicoLocal moderadorLocal;

    private final AnalizadorHeuristicoTendencias analizadorLocal;

    public ModeradorAI(ChatModel geminiChatModel, @Qualifier("mistralChatModel") ChatModel mistralChatModel, ModeradorHeuristicoLocal moderadorLocal, AnalizadorHeuristicoTendencias analizadorLocal) {
        this.geminiChatModel = geminiChatModel;
        this.mistralChatModel = mistralChatModel;
        this.moderadorLocal = moderadorLocal;
        this.analizadorLocal = analizadorLocal;
    }

    public ResultadoModeracion esContenidoOfensivo(String mensaje, String autor) {
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

        String userText = String.format("Autor: %s | Mensaje: %s", autor, mensaje);

        try {
            ChatResponse response = geminiChatModel.call(
                    new Prompt(List.of(
                            new SystemMessage(systemText),
                            new UserMessage(userText + "\\nMensaje: " + mensaje + "\\nAutor: " + autor)
                    ))
            );

            if (response == null || response.getResult() == null || response.getResult().getOutput() == null) {
                throw new RuntimeException("Respuesta inválida de Gemini");
            }

            String res = response.getResult().getOutput().getText();
            String resUpper = res == null ? "" : res.trim().toUpperCase();

            boolean bloqueado = resUpper.contains("OFENSIVO");
            return new ResultadoModeracion(
                    "FORO-HUB-BOT(Gemini)",
                    bloqueado
                            ? "Tu mensaje ha sido filtrado por contener spam, lenguaje ofensivo o indicios de suplantación de identidad!!"
                            : "OK",
                    bloqueado
            );

        } catch (Exception e) {

            try {
                ChatResponse response = mistralChatModel.call(
                        new Prompt(List.of(
                                new SystemMessage(systemText),
                                new UserMessage(userText + "\\nMensaje: " + mensaje + "\\nAutor: " + autor)
                        ))
                );

                if (response == null || response.getResult() == null || response.getResult().getOutput() == null) {
                    throw new RuntimeException("Respuesta inválida de Mistral");
                }

                String res = response.getResult().getOutput().getText();
                String resUpper = res == null ? "" : res.trim().toUpperCase();

                boolean bloqueado = resUpper.contains("OFENSIVO");
                return new ResultadoModeracion(
                        "FORO-HUB-BOT(Mistral)",
                        bloqueado
                                ? "Tu mensaje ha sido filtrado por contener spam, lenguaje ofensivo o indicios de suplantación de identidad!!!"
                                : "OK",
                        bloqueado
                );

            } catch (Exception ex) {
                boolean bloqueado = moderadorLocal.moderar(mensaje);
                return new ResultadoModeracion(
                        "HEURISTICO_LOCAL(Backup)",
                        bloqueado
                                ? "Tu mensaje ha sido filtrado por contener spam, lenguaje ofensivo o indicios de suplantación de identidad!!!!"
                                : "OK",
                        bloqueado
                );
            }
        }
    }

    // 1. Actualiza el procedimiento generarResumenEstadistico para pasar la lista de títulos al backup
    public String generarResumenEstadistico(List<String> titulos) {
        String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        try {
            return ejecutarLlamadaIA(titulos, this.geminiChatModel, "GEMINI_PRO", fecha);
        } catch (Exception e) {
            try {
                return ejecutarLlamadaIA(titulos, this.mistralChatModel, "MISTRAL_AI", fecha);
            } catch (Exception ex) {
                return generarJsonBackup(titulos, fecha);
            }
        }
    }

    private String ejecutarLlamadaIA(List<String> t, ChatModel m, String f, String d) {
        String prompt = "Responde SOLO JSON: {'tendencias':['t1','t2'], 'sugerencia':'txt', 'fuente':'" + f + "', 'fecha_analisis':'" + d + "'}";
        var r = m.call(new Prompt(List.of(new SystemMessage(prompt), new UserMessage(String.join(",", t)))));
        return r.getResult().getOutput().getText().trim();
    }

    private String generarJsonBackup(List<String> titulos, String fecha) {
// 1. Usamos el analizador heurístico para extraer las palabras clave
        List<String> listaTendencias = analizadorLocal.obtenerTendencias(titulos);

        // 2. Convertimos la lista a un String separado por comas para el JSON
        // O mejor aún, las unimos con formato de array JSON ["tema1", "tema2"]
        String tendencias = listaTendencias.stream()
                .map(t -> "\"" + t + "\"")
                .collect(java.util.stream.Collectors.joining(", "));
        return """
                {
                "tendencias": [%s],
                "sugerencia": "Los servicios de IA (GEMINI/MISTRAL) están fuera de línea. Análisis local basado en keywords.",
                "fuente": "BACKUP_LOCAL_HEURISTICO",
                "fecha_analisis": "%s"
                }
                """.formatted(tendencias, fecha);
    }
}