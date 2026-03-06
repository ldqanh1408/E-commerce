# --- Stage 1: Build (Dùng Maven để tạo file .jar) ---
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app

# Copy parent pom.xml
COPY pom.xml .

# 1. Build core-security trước
COPY core-security ./core-security
RUN mvn -f core-security/pom.xml clean install -DskipTests

# 2. Build product-service-grpc (shared gRPC library)
COPY product-service-grpc ./product-service-grpc
RUN mvn -f product-service-grpc/pom.xml clean install -DskipTests

# 3. Build cart-service
COPY cart-service ./cart-service
WORKDIR /app/cart-service
RUN mvn clean package -DskipTests

# --- Stage 2: Runtime (Chỉ chạy file .jar với JRE nhẹ) ---
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy file .jar từ Stage 1 sang Stage 2
COPY --from=build /app/cart-service/target/cart-service-0.0.1-SNAPSHOT.jar app.jar

# Biến môi trường mặc định
ENV SERVER_PORT=8080

EXPOSE 8080

# Chạy ứng dụng with JAVA_OPTS support
ENV JAVA_OPTS=""
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
