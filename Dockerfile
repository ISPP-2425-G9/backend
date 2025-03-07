FROM openjdk:23-jdk
COPY releases/caronte-0.0.1-SNAPSHOT.jar app.jar
COPY releases/caronte-0.0.2-SNAPSHOT.jar app2.jar
COPY releases/caronte-v1.1.jar v1.1.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "v1.1.jar"]
