FROM eclipse-temurin:23-jdk-alpine AS build
# Release created with this command:
# mvn clean package -DskipTests
COPY releases/caronte-s3-v1.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
