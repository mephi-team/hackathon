FROM maven:3.9.9-eclipse-temurin-24-alpine AS builder
WORKDIR /build
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:24-jre-alpine
WORKDIR /opt/app
RUN addgroup --system javauser && adduser -S -s /usr/sbin/nologin -G javauser javauser
COPY --from=builder /build/target/backend-1.0-SNAPSHOT.jar app.jar
RUN chown -R javauser:javauser .
USER javauser
ENTRYPOINT ["java", "-jar", "app.jar"]
