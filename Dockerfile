# Stage 1: Build
FROM maven:3.9.4-amazoncorretto-21 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -Dmaven.test.skip=true

# Stage 2: Runtime
FROM amazoncorretto:21-alpine
COPY --from=builder /app/target/UniScheduleService-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar", "--spring.profiles.active=prod"]