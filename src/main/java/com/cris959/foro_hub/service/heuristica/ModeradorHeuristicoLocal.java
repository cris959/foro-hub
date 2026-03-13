package com.cris959.foro_hub.service.heuristica;

import com.cris959.foro_hub.dto.ResultadoModeracion;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class ModeradorHeuristicoLocal {

    private static final List<String> PALABRAS_ALTAS = List.of("puto", "puta", "boludo", "pelotudo", "forro",
            "hijo de puta", "verga", "culo", "mierda", "jodete", "gilipollas",
            "imbecil", "idiota", "estupido", "kill", "muerte", "idiota", "idiotas",
            "administrador", "contraseña", "clic aquí", "haga clic", "enlace");

    private static final List<java.util.regex.Pattern> PATRONES_SPAM = List.of(
            java.util.regex.Pattern.compile("https?://"),
            java.util.regex.Pattern.compile("www\\."),
            java.util.regex.Pattern.compile("\\.com")
    );

    private static final Set<String> USUARIOS_SPAM = Set.of("spam", "admin", "bot");

    public ResultadoModeracion analizarTexto(String texto, String autor) {
        if (texto == null) return new ResultadoModeracion("OK", "Sin texto", false);

        String textoOriginal = texto.toLowerCase();

        // 1. LIMPIEZA BÁSICA (letras y números solamente)
        String textoLimpio = textoOriginal.replaceAll("[^a-z0-9áéíóúñ\\s]", " ").trim();

        // 2. DETECCIÓN DIRECTA (Sin depender de scores acumulativos para casos críticos)
        boolean diceSerAdmin = textoLimpio.contains("admin") || textoLimpio.contains("administrador");
        boolean tieneEnlace = textoOriginal.contains("http") || textoOriginal.contains("www") || textoOriginal.contains(".com");

        // 3. REGLA DE ORO: Si es admin falso con link, BLOQUEO TOTAL
        if (diceSerAdmin && tieneEnlace) {
            return new ResultadoModeracion("BLOQUEO_SUPLANTACION", "Intento de suplantación con enlace externo.", true);
        }

        // 4. RESTO DE VALIDACIONES (Score acumulativo)
        int score = 0;
        if (diceSerAdmin) score += 60;
        if (tieneEnlace) score += 40;

        // Validar contra tus listas PALABRAS_ALTAS y PATRONES_SPAM
        if (PALABRAS_ALTAS.stream().anyMatch(textoLimpio::contains)) score += 80;
        if (PATRONES_SPAM.stream().anyMatch(p -> p.matcher(textoOriginal).find())) {
            score += 50;
        }

        boolean bloqueado = score >= 70;

        return new ResultadoModeracion(
                bloqueado ? "BLOQUEADO_POR_SCORE" : "OK",
                "Resultado final score: " + score,
                bloqueado
        );
    }

    /**
     * Versión simplificada para fallback de AI moderadores.
     * Ignora autor (usa solo texto), devuelve boolean directo.
     */
    public boolean moderar(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return false;  // No bloquea textos vacíos
        }

        ResultadoModeracion resultado = analizarTexto(texto, "unknown");  // Autor dummy
        return resultado.bloqueado();
    }
}