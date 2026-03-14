# Stage 1: Build file JAR
FROM maven:3.9.4-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Chạy ứng dụng
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
COPY wait-for-stripe.sh .
RUN chmod +x wait-for-stripe.sh

# Khởi chạy bằng script thay vì chạy trực tiếp Java
ENTRYPOINT ["/bin/sh", "./wait-for-stripe.sh"]
