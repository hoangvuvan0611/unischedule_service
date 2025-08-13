# Stage 1: Build
FROM maven:3.9.4-amazoncorretto-21 AS builder
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM amazoncorretto:21-alpine
WORKDIR /app

# Cài Chrome & Chromedriver
RUN apk add --no-cache bash curl unzip wget \
    && apk add --no-cache chromium chromium-chromedriver \
    && ln -sf /usr/bin/chromium-browser /usr/bin/google-chrome \
    && ln -sf /usr/bin/chromedriver /usr/local/bin/chromedriver

# Copy jar
COPY --from=builder /app/target/UniScheduleService-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8801
ENTRYPOINT ["java", "-jar", "/app/app.jar", "--spring.profiles.active=prod"]
