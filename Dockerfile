# Stage 1: Build
FROM maven:3.9.4-amazoncorretto-21 AS builder
WORKDIR /app

# Copy file cấu hình Maven trước để tận dụng cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy toàn bộ source code
COPY src ./src

# Build jar (skip test để nhanh hơn)
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM amazoncorretto:21-alpine
WORKDIR /app

# Copy đúng file jar (fat jar) mà không cần fix cứng version
COPY --from=builder /app/target/*-SNAPSHOT.jar app.jar

# Mở port
EXPOSE 8801

# Chạy app với profile dev
ENTRYPOINT ["java", "-jar", "/app.jar", "--spring.profiles.active=dev"]
