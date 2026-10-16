#!/bin/sh
set -eu

echo "Importando definicoes do RabbitMQ..."

curl --fail --show-error --silent \
  --retry 30 \
  --retry-delay 2 \
  --retry-connrefused \
  --user "$RABBITMQ_USER:$RABBITMQ_PASS" \
  --header "Content-Type: application/json" \
  --data-binary @/definitions.json \
  http://rabbitmq:15672/api/definitions

echo "Importacao concluida."