package br.edu.ufpi.avaliacoes;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class ProcessadorTexto {

    public static List<String> tokenizar(String texto) {
        if (texto == null || texto.isBlank()) {
            return List.of();
        }

        String normalizado = Normalizer.normalize(
                texto.toLowerCase(Locale.ROOT),
                Normalizer.Form.NFD
        );

        normalizado = normalizado.replaceAll("\\p{M}+", "");
        normalizado = normalizado.replaceAll("[^a-z0-9\\s]", " ");
        normalizado = normalizado.trim();

        if (normalizado.isEmpty()) {
            return List.of();
        }

        return Arrays.asList(normalizado.split("\\s+"));
    }
}