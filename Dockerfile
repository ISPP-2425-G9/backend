FROM openjdk:23-jdk
# Release created with the following command:
# mvn clean package -DskipTests
COPY releases/caronte-10-03-2025-v3.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
