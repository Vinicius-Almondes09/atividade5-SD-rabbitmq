package br.edu.ufpi.avaliacoes;

public class TesteClassificador {

    public static void main(String[] args) {

        ClassificadorSentimentos modelo =
                new ClassificadorSentimentos();

        String[] frases = {"O produto nao atendeu as minhas expectativas",
            "Produto maravilhoso e de excelente qualidade",
            "Produto quebrado e atendimento horrivel",
            "O pedido chegou hoje conforme informado",
            "Estou muito satisfeito com a compra",
            "A entrega foi pessima e o produto veio com defeito",
            "A embalagem estava fechada e o produto funciona"
        };

        String[] esperados = {"negativo",
            "positivo", "negativo", "neutro",
            "positivo", "negativo", "neutro"
        };

        int acertos = 0;

        for (int i = 0; i < frases.length; i++) {

            String resultado = modelo.classificar(frases[i]);

            boolean correto = resultado.equals(esperados[i]);

            if (correto) {
                acertos++;
            }

            System.out.printf(
                "Frase: %s%nEsperado: %s | Previsto: %s | %s%n%n",
                frases[i],
                esperados[i],
                resultado,
                correto ? "CORRETO" : "DIVERGENTE"
            );
        }

        System.out.printf(
            "Resultado: %d acertos em %d testes (%.2f%%)%n",
            acertos,
            frases.length,
            100.0 * acertos / frases.length
        );
    }
}