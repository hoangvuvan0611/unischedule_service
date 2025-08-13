# Stage 1: Build
FROM zenika/alpine-chrome:with-node
# Cài Java vào đây
RUN apk add --no-cache amazon-corretto-21
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
COPY --from=builder /app/target/UniScheduleService-0.0.1-SNAPSHOT.jar app.jar

# Mở port
EXPOSE 8801

# Chạy app với profile dev
ENTRYPOINT ["java", "-jar", "/app/app.jar", "--spring.profiles.active=prod"]
