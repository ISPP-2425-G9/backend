FROM openjdk:23-jdk
COPY releases/caronte-0.0.1-SNAPSHOT.jar app.jar
COPY releases/caronte-0.0.1-RAILWAY-DB.jar app2.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app2.jar"]
