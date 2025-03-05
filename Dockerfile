FROM openjdk:23-jdk
COPY releases/caronte-0.0.1-SNAPSHOT.jar app.jar
COPY releases/caronte-v1.jar v1.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "v1.jar"]
