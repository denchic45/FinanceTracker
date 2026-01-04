FROM gradle:8.11-jdk21 AS builder
WORKDIR /home/gradle/project
COPY common /home/gradle/common
COPY backend /home/gradle/project

RUN gradle --no-daemon build -x test --stacktrace

FROM amazoncorretto:21
WORKDIR /app
COPY --from=builder /home/gradle/project/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
EXPOSE 8080