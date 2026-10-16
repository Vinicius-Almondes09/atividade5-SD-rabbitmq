package br.edu.ufpi.suporte;

import smile.classification.DiscreteNaiveBayes;
import smile.classification.DiscreteNaiveBayes.Model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassificadorSuporte {

    private static final String[] CATEGORIAS = {
        "acesso", "desempenho", "conexao"
    };

    private final Map<String, Integer> vocabulario = new HashMap<>();
    private DiscreteNaiveBayes modelo;

    public ClassificadorSuporte() {
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
                "Nao consigo acessar minha conta",
                "Esqueci minha senha de acesso",
                "Meu login nao funciona",
                "Nao consigo entrar no sistema",
                "Erro de autenticacao do usuario",
                "Minha conta esta bloqueada"
            },
            {
                "O sistema esta muito lento",
                "A pagina demora para carregar",
                "O aplicativo trava constantemente",
                "O desempenho do sistema esta ruim",
                "O programa apresenta muita lentidao",
                "O sistema demora para responder"
            },
            {
                "Minha conexao cai constantemente",
                "Nao consigo conectar ao servidor",
                "A internet esta desconectando",
                "Erro na conexao de rede",
                "O servidor esta inacessivel pela rede",
                "A conexao com o servidor foi perdida"
            }
        };

        // Constroi o vocabulario a partir dos exemplos.
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

        // Treinamento supervisionado com Smile.
        for (int classe = 0; classe < exemplos.length; classe++) {
            for (String frase : exemplos[classe]) {
                modelo.update(vetorizar(frase), classe);
            }
        }
    }

    public String classificar(String texto) {
        List<String> palavras = ProcessadorTexto.tokenizar(texto);

        if (palavras.isEmpty()) {
            throw new IllegalArgumentException(
                "Solicitacao de suporte vazia"
            );
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