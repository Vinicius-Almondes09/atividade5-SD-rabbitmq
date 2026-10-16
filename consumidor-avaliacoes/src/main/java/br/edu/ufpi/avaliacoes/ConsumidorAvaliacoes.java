package br.edu.ufpi.avaliacoes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.nio.charset.StandardCharsets;

public class ConsumidorAvaliacoes {

    private static final String FILA = "fila.avaliacoes";
    private static final int TEMPO_PROCESSAMENTO_MS = 800;

    public static void main(String[] args) throws Exception {

        ConnectionFactory factory = new ConnectionFactory();

        factory.setHost(
            System.getenv().getOrDefault("RABBITMQ_HOST", "localhost")
        );

        factory.setPort(Integer.parseInt(
            System.getenv().getOrDefault("RABBITMQ_PORT", "5672")
        ));

        factory.setUsername(
            System.getenv().getOrDefault("RABBITMQ_USER", "admin")
        );

        factory.setPassword(
            System.getenv().getOrDefault("RABBITMQ_PASS", "admin123")
        );

        ObjectMapper mapper = new ObjectMapper();
        ClassificadorSentimentos classificador =
                new ClassificadorSentimentos();

        try (Connection conexao = factory.newConnection();
             Channel canal = conexao.createChannel()) {

            // Apenas uma mensagem pendente de confirmacao por vez.
            canal.basicQos(1);

            System.out.println("Consumidor de avaliacoes iniciado.");

            DeliverCallback callback = (consumerTag, entrega) -> {

                long tag = entrega.getEnvelope().getDeliveryTag();

                try {
                    String corpo = new String(
                        entrega.getBody(),
                        StandardCharsets.UTF_8
                    );

                    JsonNode json = mapper.readTree(corpo);

                    if (json == null || !json.isObject()) {
                        throw new IllegalArgumentException("JSON invalido");
                    }

                    String id = json.path("id").asText("");
                    String tipo = json.path("tipo").asText("");
                    String conteudo = json.path("conteudo").asText("");

                    if (id.isBlank()
                            || !tipo.equals("avaliacao")
                            || conteudo.isBlank()
                            || json.path("timestamp").asText("").isBlank()) {

                        throw new IllegalArgumentException(
                            "Mensagem com campos invalidos"
                        );
                    }

                    // Simula um processamento mais lento.
                    Thread.sleep(TEMPO_PROCESSAMENTO_MS);

                    String sentimento =
                            classificador.classificar(conteudo);

                    System.out.printf(
                        "ID: %s | Sentimento: %s | Texto: %s%n",
                        id, sentimento, conteudo
                    );

                    // ACK manual somente apos o processamento.
                    canal.basicAck(tag, false);

                } catch (InterruptedException e) {

                    Thread.currentThread().interrupt();

                    // Devolve a mensagem para nova tentativa.
                    canal.basicNack(tag, false, true);
                    throw new java.io.IOException(
                        "Processamento interrompido", e
                    );

                } catch (Exception e) {

                    System.err.println(
                        "Erro no processamento: " + e.getMessage()
                    );

                    // Rejeita sem requeue: politica envia para DLQ.
                    canal.basicNack(tag, false, false);
                }
            };

            // false desativa a confirmacao automatica.
            canal.basicConsume(FILA, false, callback, consumerTag -> {});

            System.out.println(
                "Aguardando mensagens da fila: " + FILA
            );

            // Mantem a aplicacao ativa.
            Thread.currentThread().join();
        }
    }
}