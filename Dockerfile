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

# Cài đặt Chromium và dependencies
RUN apk add --no-cache \
    bash \
    curl \
    unzip \
    wget \
    chromium \
    chromium-chromedriver \
    nss \
    freetype \
    freetype-dev \
    harfbuzz \
    ca-certificates \
    ttf-freefont \
    && rm -rf /var/cache/apk/*

# Tạo symbolic links
RUN ln -sf /usr/bin/chromium-browser /usr/bin/google-chrome \
    && ln -sf /usr/bin/chromedriver /usr/local/bin/chromedriver

# Set environment variables for Chromium
ENV CHROME_BIN=/usr/bin/chromium-browser
ENV CHROME_PATH=/usr/bin/chromium-browser

# Copy jar
COPY --from=builder /app/target/UniScheduleService-0.0.1-SNAPSHOT.jar app.jar

# Tạo user non-root cho security
RUN addgroup -g 1000 spring && adduser -u 1000 -G spring -s /bin/sh -D spring
RUN chown spring:spring /app/app.jar
USER spring

EXPOSE 8801
ENTRYPOINT ["java", "-jar", "/app/app.jar", "--spring.profiles.active=prod"]