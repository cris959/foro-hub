package com.cris959.foro_hub.service.heuristica;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AnalizadorHeuristicoTendencias {

    // "Stopwords": Palabras que debemos ignorar porque no aportan valor temático
    private static final Set<String> STOP_WORDS = Set.of(
            // Conectores y preposiciones
            "sobre", "para", "como", "tengo", "duda", "consulta", "ayuda",
            "error", "problema", "esta", "este", "esto", "pero", "hacer",
            "cuando", "donde", "quien", "desde", "todo", "todos", "estos",
            // Verbos comunes en foros
            "necesito", "saber", "quiero", "puedo", "vuestro", "nuestro",
            "gracias", "saludos", "hola", "buenas"
    );

    public List<String> obtenerTendencias(List<String> titulos) {
        if (titulos == null || titulos.isEmpty()) return List.of("Sin datos");

        // 1. Unir, limpiar y separar palabras
        String textoCompleto = String.join(" ", titulos).toLowerCase();

        // Removemos caracteres especiales pero mantenemos espacios
        String soloPalabras = textoCompleto.replaceAll("[^a-z0-9áéíóúñ\\s]", " ");

        // 2. Procesar el flujo de palabras
        Map<String, Long> conteo = Arrays.stream(soloPalabras.split("\\s+"))
                .map(String::trim)
                .filter(p -> p.length() > 3) // Ignorar palabras cortas (de, el, al, api)
                .filter(p -> !STOP_WORDS.contains(p)) // Ignorar palabras comunes
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        // 3. Ordenar por frecuencia y devolver las 3 más repetidas
        return conteo.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}