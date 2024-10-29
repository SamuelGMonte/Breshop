# Usar o OpenJDK 23 com Maven instalado manualmente
FROM openjdk:23-jdk-slim AS build

# Instalar Maven e outras dependências necessárias
RUN apt-get update && \
    apt-get install -y maven && \
    rm -rf /var/lib/apt/lists/*

# Copiar arquivos do projeto para o contêiner
COPY src /home/app/src
COPY pom.xml /home/app

# Executar o build do Maven sem rodar os testes
RUN mvn -f /home/app/pom.xml clean package -DskipTests

# Imagem final apenas com o JDK 23
FROM openjdk:23-jdk-slim
COPY --from=build /home/app/target/breshop-0.0.1-SNAPSHOT.jar /home/app/breshop.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/home/app/breshop.jar"]
