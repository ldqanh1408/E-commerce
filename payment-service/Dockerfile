# --- Stage 1: Build (Dùng Maven để tạo file .jar) ---
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app

# 1. Build core-security trước (Bắt buộc vì payment-service phụ thuộc vào nó)
COPY core-security ./core-security
RUN mvn -f core-security/pom.xml clean install -DskipTests

# 2. Build payment-api (thư viện API mà payment-service phụ thuộc)
COPY payment-api ./payment-api
RUN mvn -f payment-api/pom.xml clean install -DskipTests

# 3. Build payment-service
COPY payment-service ./payment-service
WORKDIR /app/payment-service

# Build package
RUN mvn clean package -DskipTests

# --- Stage 2: Runtime (Chỉ chạy file .jar với JRE nhẹ) ---
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy file .jar từ Stage 1 sang Stage 2
COPY --from=build /app/payment-service/target/*.jar app.jar

# Biến môi trường mặc định
ENV SERVER_PORT=8080

EXPOSE 8080

# Chạy ứng dụng with JAVA_OPTS support
ENV JAVA_OPTS=""
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
