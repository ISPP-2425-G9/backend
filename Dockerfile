FROM openjdk:23-jdk-slim AS jre-builder
RUN apt-get update && apt-get install -y binutils && rm -rf /var/lib/apt/lists/*

RUN jlink --module-path "$JAVA_HOME/jmods" \
  --add-modules java.base,java.logging \
  --strip-debug --no-header-files --no-man-pages \
  --output /customjre

FROM debian:bullseye-slim
COPY --from=jre-builder /customjre /customjre
COPY releases/caronte-s3-v1.jar app.jar
ENV PATH="/customjre/bin:$PATH"
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]