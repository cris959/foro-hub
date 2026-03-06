package com.cris959.foro_hub.service.moderacion;

import com.cris959.foro_hub.infra.springai.ModeradorAI;
import com.cris959.foro_hub.repository.TopicoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class TopicoModeradorAI implements ITopicoModeradorAI {

    private final TopicoRepository topicoRepository;

    private final ModeradorAI analistaIA;

    public TopicoModeradorAI(TopicoRepository topicoRepository, ModeradorAI analistaIA) {
        this.topicoRepository = topicoRepository;
        this.analistaIA = analistaIA;
    }

    public String obtenerAnalisisDeTendencias() {
// 1. Obtener y formatear la hora local
        String horaLocal = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

        var titulos = topicoRepository.findAll().stream().map(t -> t.getTitulo()).toList();

        if (titulos.isEmpty()) {
            return "{'mensaje': 'No hay datos.', 'generado_en': '" + horaLocal + "'}";
        }

        int cantidad = Math.min(titulos.size(), 10);

        List<String> muestra = titulos.subList(titulos.size() - cantidad, titulos.size());

        try {
            String resultadoIA = analistaIA.generarResumenEstadistico(muestra);
            return resultadoIA;
        } catch (Exception e) {
            System.out.println("ALERTA: IA saturada. Iniciando analista de respaldo...");
            return generarAnalisisManual(muestra);
        }
    }

    private String generarAnalisisManual(List<String> ultimosDiez) {

        String fecha = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

        String textoUnido = String.join(" ", ultimosDiez).toLowerCase();

        List<String> keywords = List.of("java", "spring", "jpa", "docker", "jwt", "api", "node");

        List<String> ranking = keywords.stream()
                .map(word -> {
                    long count = java.util.Arrays.stream(textoUnido.split("\\P{L}+"))
                            .filter(word::equals).count();
                    return new java.util.AbstractMap.SimpleEntry<>(word, count);
                })
                .filter(e -> e.getValue() > 0)
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(3)
                .map(e -> e.getKey() + " (" + e.getValue() + " menciones)")
                .toList();

        String tendenciasFinales = ranking.isEmpty() ? "Temas generales" : String.join(", ", ranking);
//                String.join(", ", ranking);

        return """
                {
                 "popularidad": ["%s"],
                 "sugerencia": "El servicio de IA está en mantenimiento, pero detectamos fuerte actividad en estos temas. ¡Sigue participando!",
                 "fuente": "SISTEMA_LOCAL_BACKUP",
                 "fecha_analisis": "%s"
                 }
                """.formatted(tendenciasFinales, fecha);
    }
}
