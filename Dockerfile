FROM openjdk:23-jdk-slim AS build

RUN apt-get update && apt-get install -y curl tar

RUN mkdir -p /usr/share/maven && \
    curl -fsSL https://archive.apache.org/dist/maven/maven-3/3.8.8/binaries/apache-maven-3.8.8-bin.tar.gz \
    | tar -xzC /usr/share/maven --strip-components=1 && \
    ln -s /usr/share/maven/bin/mvn /usr/bin/mvn

RUN mvn -version

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM openjdk:23-jdk-slim
WORKDIR /app
COPY --from=build /app/target/caronte-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
