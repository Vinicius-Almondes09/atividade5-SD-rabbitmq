package br.edu.ufpi.avaliacoes;

import smile.classification.DiscreteNaiveBayes;
import smile.classification.DiscreteNaiveBayes.Model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassificadorSentimentos {

    private static final String[] CATEGORIAS = {
        "positivo", "negativo", "neutro"
    };

    private final Map<String, Integer> vocabulario = new HashMap<>();
    private DiscreteNaiveBayes modelo;

    public ClassificadorSentimentos() {
        treinar();
    }

    private int[] vetorizar(String texto) {
        int[] vetor = new int[vocabulario.size()];

        for (String palavra : ProcessadorTexto.tokenizar(texto)) {
            Integer indice = vocabulario.get(palavra);

            if (indice != null) {
                vetor[indice]++;
            }
        }

        return vetor;
    }

    private void treinar() {
        String[][] exemplos = {
            {
                "Produto excelente e de otima qualidade",
                "Gostei muito do produto",
                "Entrega rapida e produto maravilhoso",
                "Muito bom recomendo a compra",
                "Estou satisfeito com a qualidade",
                "Produto perfeito e excelente"
            },
            {
                "Produto com defeito e pessima qualidade",
                "Nao gostei do produto",
                "Entrega atrasada e atendimento ruim",
                "Produto horrivel nao recomendo",
                "Estou insatisfeito com a compra",
                "Produto quebrado e de baixa qualidade",
                "O produto nao atendeu as minhas expectativas",
                "O produto ficou abaixo das minhas expectativas",
                "Estou decepcionado com o produto",
                "A compra nao correspondeu ao que esperava"
            },
            {
                "Produto chegou no prazo",
                "O produto funciona normalmente",
                "Recebi o pedido hoje",
                "Produto conforme a descricao",
                "A embalagem chegou fechada",
                "O produto apresenta caracteristicas comuns"
            }
        };

        // Constroi o vocabulario a partir dos exemplos rotulados.
        for (String[] grupo : exemplos) {
            for (String frase : grupo) {
                for (String palavra : ProcessadorTexto.tokenizar(frase)) {
                    vocabulario.computeIfAbsent(
                        palavra, p -> vocabulario.size()
                    );
                }
            }
        }

        modelo = new DiscreteNaiveBayes(
            Model.MULTINOMIAL,
            CATEGORIAS.length,
            vocabulario.size()
        );

        // Treinamento supervisionado com a biblioteca Smile.
        for (int classe = 0; classe < exemplos.length; classe++) {
            for (String frase : exemplos[classe]) {
                modelo.update(vetorizar(frase), classe);
            }
        }
    }

    public String classificar(String texto) {
        List<String> palavras = ProcessadorTexto.tokenizar(texto);

        if (palavras.isEmpty()) {
            throw new IllegalArgumentException("Texto vazio");
        }

        int[] vetor = vetorizar(texto);
        int classe = modelo.predict(vetor);

        if (classe < 0 || classe >= CATEGORIAS.length) {
            throw new IllegalArgumentException(
                "Texto sem palavras reconhecidas pelo modelo"
            );
        }

        return CATEGORIAS[classe];
    }
}