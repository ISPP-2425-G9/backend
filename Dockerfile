FROM openjdk:23-jdk
# Release created with this command:
# mvn clean package -DskipTests
COPY releases/caronte-wpl-v2.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]