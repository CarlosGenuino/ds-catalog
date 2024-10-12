# Usando uma imagem base do Maven para compilar o projeto
FROM maven:3.8.4-openjdk-17 AS build

# Definindo o diretório de trabalho
WORKDIR /app

# Copiando os arquivos de configuração do Maven e do projeto para o container
COPY pom.xml .
COPY src ./src

# Compilando o projeto
RUN mvn clean package -DskipTests

# Usando uma imagem mais leve do OpenJDK para rodar o projeto
FROM openjdk:17-jdk-alpine

# Definindo o diretório de trabalho
WORKDIR /app

# Copiando o arquivo JAR do estágio anterior
COPY --from=build /app/target/*.jar /app/ds-catalog-0.0.1-SNAPSHOT.jar

# Especificando o comando para executar o aplicativo
ENTRYPOINT ["java", "-jar", "/app/ds-catalog-0.0.1-SNAPSHOT.jar"]