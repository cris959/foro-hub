package com.cris959.foro_hub.service.moderacion;

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

    private static final List<String> PATRONES_SPAM = List.of("https?://", "www\\.", "\\.com", "\\.net",
            "\\.org", "ganá\sdinero", "clic\saquí", "gratis", "oferta");

    private static final Set<String> USUARIOS_SPAM = Set.of("spam", "admin", "bot");

    public ResultadoModeracion analizarTexto(String texto, String autor) {

        String textoLimpio = texto.toLowerCase().replaceAll("[^a-zA-Z0-9áéíóúñ\\\\s]", " ").trim();
        boolean tieneInsultos = PALABRAS_ALTAS.stream().anyMatch(palabra -> textoLimpio.contains(palabra));
        boolean tieneSpam = PATRONES_SPAM.stream().anyMatch(patron -> java.util.regex.Pattern
                .compile(patron).matcher(texto).find());

        boolean autorSospechoso = USUARIOS_SPAM.stream().anyMatch(autor.toLowerCase()::contains);
        boolean textoCorto = textoLimpio.length() < 10;
        int score = 0;

        if (textoLimpio.contains("administrador") || textoLimpio.contains("admin")) score += 50;
        if (textoLimpio.contains("clic") || textoLimpio.contains("enlace")) score += 30;

        if (tieneInsultos) score += 80;
        if (tieneSpam) score += 35;
        if (autorSospechoso) score += 20;
        if (textoCorto) score += 15;
        boolean bloqueado = score > 70;
        return new ResultadoModeracion(bloqueado ? "BACKUP_LOCAL" : "BACKUP_OK",
                "Se detectó contenido inapropiado, lenguaje ofensivo o spam en su publicación(Score: " + score + ").",

                bloqueado
        );
    }
}

