# =========================
# Stage 1: Build jar
# =========================
FROM maven:3.9.4-amazoncorretto-21 AS builder
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests

# =========================
# Stage 2: Runtime
# =========================
FROM amazoncorretto:21-jdk AS runtime
WORKDIR /app

# Cài Chromium và các thư viện cần thiết trên Debian
RUN apt-get update && apt-get install -y \
    chromium \
    libnss3 \
    libglib2.0-0 \
    libx11-6 \
    libxcomposite1 \
    libxdamage1 \
    libxrandr2 \
    libasound2 \
    libatk1.0-0 \
    libcups2 \
    libdbus-1-3 \
    libexpat1 \
    libfontconfig1 \
    libgcc-s1 \
    libgconf-2-4 \
    libgdk-pixbuf2.0-0 \
    libgtk-3-0 \
    libnspr4 \
    libpango-1.0-0 \
    libxau6 \
    libxdmcp6 \
    libbsd0 \
    libintl8 \
    libpcre2-8-0 \
    && rm -rf /var/lib/apt/lists/*

COPY --from=builder /app/target/UniScheduleService-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8801

ENTRYPOINT ["java", "-jar", "/app/app.jar", "--spring.profiles.active=prod"]