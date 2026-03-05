# --- Stage 1: Build (Dùng Maven để tạo file .jar) ---
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app

# 1. Build core-security trước (vì order-service phụ thuộc vào nó)
COPY core-security ./core-security
RUN mvn -f core-security/pom.xml clean install -DskipTests

# 2. Build order-service
# Copy toàn bộ thư mục order-service vào container
COPY order-service ./order-service

# Chuyển working directory vào thư mục order-service
WORKDIR /app/order-service

# Build package, bỏ qua test để nhanh hơn
RUN mvn clean package -DskipTests

# --- Stage 2: Runtime (Chỉ chạy file .jar với JRE nhẹ) ---
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy file .jar từ Stage 1 sang Stage 2
# Lưu ý đường dẫn nguồn thay đổi vì WORKDIR ở stage 1 đã thay đổi
COPY --from=build /app/order-service/target/*.jar app.jar

# Biến môi trường mặc định (sẽ bị override bởi docker-compose)
ENV SERVER_PORT=8080

EXPOSE 8080

# Chạy ứng dụng with JAVA_OPTS support
ENV JAVA_OPTS=""
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
