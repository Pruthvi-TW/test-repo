FROM eclipse-temurin:21-jdk-jammy

# Build stage
FROM maven:3.9.4-eclipse-temurin-21-jammy AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Add non-root user for security
RUN groupadd -r appgroup && useradd -r -g appgroup appuser

# Install required packages
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Copy JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Set environment variables
ENV JAVA_OPTS="-XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+UseStringDeduplication -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/tmp/heapdump.hprof"
ENV SPRING_PROFILES_ACTIVE="prod"
ENV TZ="UTC"

# Create necessary directories with proper permissions
RUN mkdir -p /app/logs /app/config && \
    chown -R appuser:appgroup /app

# Switch to non-root user
USER appuser

# Health check
HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Expose application port
EXPOSE 8080

# Set entry point
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar"]

# Labels
LABEL maintainer="eKYC Development Team" \
      version="1.0" \
      description="eKYC Verification Service" \
      org.opencontainers.image.source="https://github.com/organization/ekyc-verification-service" \
      org.opencontainers.image.vendor="Organization Name" \
      org.opencontainers.image.title="eKYC Verification Service" \
      org.opencontainers.image.description="Service for eKYC verification and authentication" \
      org.opencontainers.image.version="1.0" \
      org.opencontainers.image.created="2024-02-23T10:00:00Z"

# Volume configuration for persistent data
VOLUME ["/app/logs", "/app/config"]