FROM openjdk:23-jdk
# Release created with the following command:
# mvn clean package -DskipTests
COPY releases/caronte-sprint1-final.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
