# Atividade 5 — Sistema Distribuído de Classificação Inteligente de Mensagens com RabbitMQ

**Disciplina:** Sistemas Distribuídos  
**Curso:** Sistemas de Informação — UFPI  
**Modalidade:** Individual

## 1. Objetivo

Desenvolver um sistema distribuído utilizando Java, Docker, RabbitMQ e Inteligência Artificial, capaz de gerar, distribuir e classificar automaticamente avaliações de produtos e solicitações de suporte.

## 2. Arquitetura

O sistema possui quatro serviços principais:

1. RabbitMQ: gerenciamento e distribuição das mensagens.
2. Gerador Java: produção contínua de mensagens.
3. Consumidor de avaliações: análise de sentimentos.
4. Consumidor de suporte: classificação de problemas técnicos.

O serviço auxiliar rabbitmq-init configura automaticamente os recursos de mensageria e encerra após concluir a importação.

## 3. Tecnologias utilizadas

- Java 21 e Apache Maven.
- Docker e Docker Compose.
- RabbitMQ 4.1 com Management Plugin.
- RabbitMQ Java Client.
- Jackson para processamento JSON.
- Smile 3.1.1 para Inteligência Artificial.
- Algoritmo Naive Bayes Multinomial.

## 4. Geração e roteamento

O gerador produz mensagens JSON contendo identificador UUID, data e hora, tipo e conteúdo textual.

As mensagens são publicadas no Topic Exchange mensagens.topic.

- review.product: encaminha avaliações para fila.avaliacoes.
- support.request: encaminha solicitações para fila.suporte.

O intervalo configurado entre publicações é de 180 ms. Em uma medição realizada, observou-se uma taxa aproximada de 5,8 mensagens por segundo.

## 5. Classificadores de Inteligência Artificial

O consumidor de avaliações utiliza Smile e Naive Bayes Multinomial para classificar sentimentos como positivo, negativo ou neutro.

O consumidor de suporte possui outro classificador, também com Smile, para identificar problemas de acesso, desempenho ou conexão.

Os classificadores utilizam exemplos rotulados para treinamento e representação numérica dos textos.

Nos testes funcionais, a IA de avaliações apresentou 7 acertos em 7 casos e a IA de suporte, 6 acertos em 6 casos. Esses resultados não representam a precisão geral dos modelos.

## 6. ACK manual e DLQ

Os consumidores utilizam basicConsume com autoAck=false e basicQos(1).

O basicAck confirma mensagens processadas corretamente. Em caso de erro, basicNack com requeue=false rejeita a mensagem.

O RabbitMQ utiliza o exchange mensagens.dlx, a routing key mensagem.erro e a fila fila.dlq para armazenar mensagens rejeitadas.

O tratamento automático de mensagens inválidas foi testado durante o desenvolvimento.
## 7. Crescimento das filas

Os consumidores processam as mensagens em aproximadamente 800 ms cada, permitindo que a taxa de geração supere a taxa de consumo.

Durante os testes, foram observados os seguintes resultados:

| Fila | Primeira medição | Segunda medição | Variação |
|---|---:|---:|---:|
| fila.avaliacoes | 643 | 759 | +116 |
| fila.suporte | 757 | 872 | +115 |
| fila.dlq | 2 | 2 | 0 |

As duas filas principais apresentaram crescimento total de 231 mensagens.

## 8. Instruções de execução

### Pré-requisitos

- Docker Desktop ou Docker Engine.
- Docker Compose.
- Internet para baixar as imagens e dependências na primeira execução.

### Preparação

Após baixar ou clonar o repositório, acesse a pasta do projeto.

No PowerShell, execute:

    Copy-Item .env.example .env

Edite o arquivo .env e configure uma senha local para RABBITMQ_PASS.

### Iniciar o sistema

    docker compose config --quiet
    docker compose up -d --build

O Docker Compose inicia o RabbitMQ, executa o serviço auxiliar rabbitmq-init e, após a importação das definições, inicia o gerador e os dois consumidores.

### Verificar os serviços

    docker compose ps -a

Resultado esperado:

- RabbitMQ: Up (healthy).
- Gerador: Up.
- Consumidor de avaliações: Up.
- Consumidor de suporte: Up.
- Rabbitmq-init: Exited (0).

### Consultar os logs

    docker compose logs --tail 20 gerador
    docker compose logs --tail 20 consumidor-avaliacoes
    docker compose logs --tail 20 consumidor-suporte

### Interface RabbitMQ

Endereço: http://localhost:15672

Utilize as credenciais configuradas no arquivo .env.

### Encerrar a execução

    docker compose stop

Esse comando interrompe os serviços sem excluir o volume persistente.

## 9. Validação em instalação limpa

Foi realizado um teste com projeto Docker Compose independente, uma rede nova, volume RabbitMQ novo e portas alternativas.

Os resultados confirmaram:

- RabbitMQ saudável.
- Serviço rabbitmq-init encerrado com código zero.
- Quatro serviços principais em execução.
- Mensagens recebidas nas filas de avaliações e suporte.
- Classificações apresentadas pelos dois consumidores.

Em uma consulta, cada fila principal continha 53 mensagens aguardando processamento.

O ambiente de validação foi removido após os testes, sem afetar o ambiente principal.

## 10. Evidências

As capturas de tela da implementação e dos testes estão disponíveis na pasta evidencias/ do repositório.

A pasta contém registros dos containers Docker, testes das duas IAs, geração de mensagens, interface do RabbitMQ, Topic Exchange, bindings, crescimento das filas e encaminhamento de mensagens para a DLQ.

As imagens não foram incorporadas ao README, permanecendo disponíveis separadamente para consulta.

## 11. Demonstração

A demonstração do funcionamento contempla:

- Geração contínua de mensagens.
- Roteamento pelo Topic Exchange.
- Classificação das duas IAs.
- Crescimento das filas.
- Tratamento de mensagens inválidas pela DLQ.

O vídeo de aproximadamente cinco minutos será disponibilizado separadamente, conforme a organização da entrega.

## 12. Considerações finais

O projeto implementa um sistema distribuído orientado a mensagens, com quatro serviços principais executados em containers Docker.

Os testes realizados demonstraram a geração, o encaminhamento e a classificação das mensagens, além da confirmação manual de consumo, do tratamento de falhas pela DLQ e do crescimento das filas.

A execução em ambiente isolado com volume novo também foi validada.

Os modelos de IA utilizam conjuntos reduzidos de treinamento e testes, adequados à demonstração acadêmica. Os resultados apresentados não representam uma avaliação estatística ampla da precisão dos classificadores.

## 13. Referências técnicas

- Docker: https://docs.docker.com/
- Docker Compose: https://docs.docker.com/compose/
- RabbitMQ: https://www.rabbitmq.com/docs
- RabbitMQ Management: https://www.rabbitmq.com/docs/management
- RabbitMQ DLX: https://www.rabbitmq.com/docs/dlx
- Smile: https://haifengl.github.io/
- Apache Maven: https://maven.apache.org/
## 14. Informações complementares da implementação

### 14.1 Independência dos classificadores

O sistema utiliza dois classificadores independentes, desenvolvidos em Java com a biblioteca Smile 3.1.1 e o algoritmo Naive Bayes Multinomial.

O consumidor de avaliações utiliza exemplos rotulados para classificar sentimentos em positivo, negativo ou neutro.

O consumidor de suporte utiliza outro conjunto de treinamento, específico para classificar solicitações em problemas de acesso, desempenho ou conexão.

Cada consumidor possui seu próprio processamento textual, treinamento e execução, sem depender das classificações realizadas pelo outro.

### 14.2 Comunicação entre os containers

Os quatro serviços principais são executados em containers Docker e conectados à rede compartilhada `rede-sistema`, definida no arquivo `compose.yaml`.

A comunicação com o RabbitMQ ocorre utilizando o nome do serviço `rabbitmq` como endereço interno do broker.

O serviço auxiliar `rabbitmq-init` também participa dessa rede durante a configuração inicial, importando as definições antes da execução dos serviços Java.

### 14.3 Demonstração do tratamento de erros

O sistema utiliza confirmação manual de consumo e encaminhamento de mensagens inválidas para uma Dead Letter Queue.

Quando ocorre um erro de processamento, o consumidor rejeita a mensagem utilizando `basicNack` com `requeue=false`.

A mensagem rejeitada é encaminhada ao exchange `mensagens.dlx` e armazenada na fila `fila.dlq`.

O resultado pode ser verificado na interface administrativa do RabbitMQ, acessível em `http://localhost:15672`, na seção de filas.

O mecanismo foi validado durante os testes com mensagens inválidas.

### 14.4 Organização das evidências

As evidências estão armazenadas separadamente na pasta `evidencias/`, sem imagens incorporadas ao README.

Entre os principais arquivos estão:

- `quatro-containers-terminal.jpg`: execução dos containers.
- `taxa-geracao.jpg`: medição da geração de mensagens.
- `teste-ia.jpg`: teste da IA de avaliações.
- `teste-ia-suporte.jpg`: teste da IA de suporte.
- `rabbitmq-interface.jpg`: interface administrativa.
- `topic-exchange.jpg`: configuração do Topic Exchange.
- `bindings-rabbitmq.jpg`: bindings e roteamento.
- `crescimento-das-filas.jpg`: acúmulo de mensagens.
- `teste-dlq-automatica.jpg`: rejeição automática para a DLQ.
- `teste-dlq.jpg`: teste do mecanismo de dead-lettering.

Essas capturas complementam os resultados apresentados nas seções anteriores e permitem verificar visualmente a execução do sistema.
