# Paso 1: Compilar la aplicación usando Maven con Java 17
FROM maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

# Paso 2: Ejecutar la aplicación usando Eclipse Temurin (Java 17 oficial y ligero)
FROM eclipse-temurin:17-jre-alpine
COPY --from=build /target/invenatario-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]