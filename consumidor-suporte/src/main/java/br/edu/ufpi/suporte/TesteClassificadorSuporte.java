package br.edu.ufpi.suporte;

public class TesteClassificadorSuporte {

    public static void main(String[] args) {

        ClassificadorSuporte modelo = new ClassificadorSuporte();

        String[] frases = {
            "Nao consigo fazer login na minha conta",
            "Minha senha foi esquecida e nao consigo entrar",
            "O aplicativo esta muito lento para abrir",
            "O sistema apresenta travamentos e demora",
            "A conexao com a internet cai frequentemente",
            "Nao consigo estabelecer conexao com o servidor"
        };

        String[] esperados = {
            "acesso",
            "acesso",
            "desempenho",
            "desempenho",
            "conexao",
            "conexao"
        };

        int acertos = 0;

        for (int i = 0; i < frases.length; i++) {

            String previsto = modelo.classificar(frases[i]);
            boolean correto = esperadoIgual(previsto, esperados[i]);

            if (correto) {
                acertos++;
            }

            System.out.printf(
                "Solicitacao: %s%nEsperado: %s | Previsto: %s | %s%n%n",
                frases[i],
                esperados[i],
                previsto,
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

    private static boolean esperadoIgual(
            String previsto, String esperado) {
        return esperado.equals(previsto);
    }
}