# Stage 1: Build
FROM maven:3.9.4-amazoncorretto-21 AS builder
WORKDIR /app

# Copy pom.xml trước để cache dependency
COPY pom.xml .
RUN mvn dependency:go-offline -B --no-transfer-progress

# Copy source code
COPY src ./src

# Build ứng dụng
RUN mvn clean package -Dmaven.test.skip=true --no-transfer-progress

# Stage 2: Runtime
FROM amazoncorretto:21-alpine
WORKDIR /app

# Copy jar từ stage build
COPY --from=builder /app/target/UniScheduleService-0.0.1-SNAPSHOT.jar app.jar

# Mở port (giúp người khác đọc config dễ hơn)
EXPOSE 8801

# Chạy ứng dụng
ENTRYPOINT ["java", "-jar", "/app.jar", "--spring.profiles.active=dev"]
