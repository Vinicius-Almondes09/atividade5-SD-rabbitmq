package br.edu.ufpi.gerador;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class GeradorMensagens {

    private static final String EXCHANGE = "mensagens.topic";
    private static final int INTERVALO_MS = 180;

    private static final String[] AVALIACOES = {
        "Produto excelente, entrega rapida e qualidade muito boa.",
        "Produto com defeito, pessima qualidade.",
        "Produto chegou no prazo e funciona normalmente.",
        "Gostei muito do produto, recomendo a compra.",
        "O produto nao atendeu as minhas expectativas."
    };

    private static final String[] SUPORTE = {
        "Nao consigo acessar minha conta.",
        "O sistema esta muito lento.",
        "Minha conexao cai constantemente.",
        "Esqueci minha senha de acesso.",
        "A pagina demora muito para carregar.",
        "Nao consigo conectar ao servidor."
    };

    public static void main(String[] args) throws Exception {

        ConnectionFactory factory = new ConnectionFactory();

        factory.setHost(
            System.getenv().getOrDefault("RABBITMQ_HOST", "localhost")
        );

        factory.setPort(
            Integer.parseInt(
                System.getenv().getOrDefault("RABBITMQ_PORT", "5672")
            )
        );

        factory.setUsername(
            System.getenv().getOrDefault("RABBITMQ_USER", "admin")
        );

        factory.setPassword(
            System.getenv().getOrDefault("RABBITMQ_PASS", "admin123")
        );

        ObjectMapper mapper = new ObjectMapper();

        try (Connection conexao = factory.newConnection();
             Channel canal = conexao.createChannel()) {

            System.out.println("Gerador conectado ao RabbitMQ.");

            long contador = 0;

            while (true) {

                boolean avaliacao = contador % 2 == 0;

                String tipo = avaliacao ? "avaliacao" : "suporte";

                String routingKey = avaliacao
                        ? "review.product"
                        : "support.request";

                String[] exemplos = avaliacao
                        ? AVALIACOES
                        : SUPORTE;

                String conteudo = exemplos[
                        ThreadLocalRandom.current().nextInt(exemplos.length)
                ];

                Map<String, String> mensagem = Map.of(
                        "id", UUID.randomUUID().toString(),
                        "timestamp", Instant.now().toString(),
                        "tipo", tipo,
                        "conteudo", conteudo
                );

                String json = mapper.writeValueAsString(mensagem);

                canal.basicPublish(
                        EXCHANGE,
                        routingKey,
                        MessageProperties.PERSISTENT_TEXT_PLAIN,
                        json.getBytes(StandardCharsets.UTF_8)
                );

                contador++;

                System.out.printf(
                        "[%d] %s -> %s%n",
                        contador,
                        routingKey,
                        json
                );

                Thread.sleep(INTERVALO_MS);
            }
        }
    }
}
