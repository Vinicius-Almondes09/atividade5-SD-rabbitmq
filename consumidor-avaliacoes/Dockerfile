# Etapa 1 - Compilacao
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn -B clean package dependency:copy-dependencies -DskipTests

# Etapa 2 - Execucao
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/target/classes ./classes
COPY --from=build /app/target/dependency ./dependency

ENTRYPOINT ["java", "-cp", "/app/classes:/app/dependency/*", "br.edu.ufpi.avaliacoes.ConsumidorAvaliacoes"]