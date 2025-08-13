# =========================
# Stage 1: Build jar
# =========================
FROM maven:3.9.4-amazoncorretto-21 AS builder
WORKDIR /app

# Copy file pom trước để cache dependencies Maven
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build jar (skip tests)
RUN mvn clean package -DskipTests

# =========================
# Stage 2: Runtime
# =========================
FROM amazoncorretto:21-alpine-jdk AS runtime
WORKDIR /app

# Cài Chromium và thư viện cần thiết (tách thành layer riêng để cache được)
RUN apk add --no-cache \
    chromium \
    nss \
    freetype \
    harfbuzz \
    ttf-freefont \
    && rm -rf /var/cache/apk/*

# Copy jar từ stage build
COPY --from=builder /app/target/UniScheduleService-0.0.1-SNAPSHOT.jar app.jar

# Mở port
EXPOSE 8801

# Entrypoint
ENTRYPOINT ["java", "-jar", "/app/app.jar", "--spring.profiles.active=prod"]
