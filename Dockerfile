FROM openjdk:23-jdk-slim
# Release created with this command:
# mvn clean package -DskipTests
COPY releases/caronte-s3-v1.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=70.0", "-XX:InitialRAMPercentage=50.0", "-jar", "app.jar"]