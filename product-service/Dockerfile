# --- Stage 1: Build ---
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app

# 1. Build core-security (Giả định product-service cũng cần lib này)
COPY core-security ./core-security
RUN mvn -f core-security/pom.xml clean install -DskipTests

# 2. Build product-service-grpc (BẮT BUỘC: Vì Product Service implement gRPC interface này)
COPY product-service-grpc ./product-service-grpc
RUN mvn -f product-service-grpc/pom.xml clean install -DskipTests

# 3. Build product-service
COPY product-service ./product-service
WORKDIR /app/product-service
RUN mvn clean package -DskipTests

# --- Stage 2: Runtime ---
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/product-service/target/*.jar app.jar
ENV SERVER_PORT=8080
EXPOSE 8080
ENV JAVA_OPTS=""
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]