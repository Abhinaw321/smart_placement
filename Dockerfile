# Multi-stage Dockerfile for Spring Boot (Smart Placement System)
# Java 21 JDK build stage -> Minimal Java 21 JRE runtime stage

# Stage 1: Build JAR package
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app

# Copy maven wrapper and project descriptor for offline dependency caching
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x ./mvnw && ./mvnw dependency:go-offline -B || true

# Copy source code and package application skipping unit tests (pre-verified in CI)
COPY src/ ./src/
RUN ./mvnw clean package -DskipTests -B

# Stage 2: Secure Slim Runtime
FROM eclipse-temurin:21-jre-jammy AS runtime
WORKDIR /app

# Create unprivileged non-root user and persistent upload directory
RUN useradd -r -u 1001 -m -s /bin/bash appuser && \
    mkdir -p /app/uploads/resumes && \
    chown -R appuser:appuser /app

# Copy the built jar artifact with ownership assigned to appuser
COPY --from=build --chown=appuser:appuser /app/target/*.jar /app/app.jar

# Switch to non-root execution
USER 1001:1001

# Default environment configuration (Platform overrides PORT dynamically)
ENV PORT=8080
ENV SPRING_PROFILES_ACTIVE=prod
EXPOSE ${PORT}

# Launch JVM container with container-aware memory tuning and dynamic port binding
ENTRYPOINT ["sh", "-c", "java -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Dserver.port=${PORT} -jar /app/app.jar"]
