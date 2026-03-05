# --- Stage 1: Build (Dùng Maven để tạo file .jar) ---
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app
# Build core-security
COPY core-security ./core-security
RUN mvn -f core-security/pom.xml clean install -DskipTests

# Build api-gateway
COPY api-gateway/pom.xml .
COPY api-gateway/src ./src
# Build package, bỏ qua test để nhanh hơn
RUN mvn clean package -DskipTests

# --- Stage 2: Runtime (Chỉ chạy file .jar với JRE nhẹ) ---
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# Copy file .jar từ Stage 1 sang Stage 2
COPY --from=build /app/target/*.jar app.jar

# Install curl for healthcheck
RUN apk add --no-cache curl

# Biến môi trường mặc định (sẽ bị override bởi docker-compose)
ENV SERVER_PORT=8080

# Hỗ trợ JAVA_OPTS để config memory, remote debug...
ENV JAVA_OPTS=""
# Port mặc định
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
ENTRYPOINT ["java", "-jar", "app.jar"]