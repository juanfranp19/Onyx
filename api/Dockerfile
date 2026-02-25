# ---- Build stage ----
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copiamos archivos del proyecto
COPY pom.xml .
COPY src ./src

# Construimos el jar
RUN mvn clean package -DskipTests

# ---- Run stage ----
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copiamos el jar generado
COPY --from=build /app/target/*.jar app.jar

# Puerto dinámico de Render
EXPOSE 8080

# Arrancar aplicación
CMD ["sh", "-c", "java -Dserver.port=$PORT -jar app.jar"]