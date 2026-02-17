# Image Java officielle
FROM eclipse-temurin:21-jdk-alpine

# Répertoire de travail
WORKDIR /app

# Copie du jar généré par Maven
COPY target/front-service-0.0.1-SNAPSHOT.jar app.jar

# Exposition du port
EXPOSE 8082

# Lancement
ENTRYPOINT ["java","-jar","app.jar"]
