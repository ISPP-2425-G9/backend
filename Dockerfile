FROM openjdk:23-jre-slim
COPY releases/caronte-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
