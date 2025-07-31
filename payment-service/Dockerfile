# Simple single-stage build for Payment Service
FROM eclipse-temurin:17-jdk-alpine

# Set working directory
WORKDIR /app

# Install curl for health checks
RUN apk --no-cache add curl

# Copy everything
COPY . .

# Make mvnw executable
RUN chmod +x mvnw

# Build the application
RUN ./mvnw clean package -DskipTests

# Expose port
EXPOSE 8082

# Run the application
CMD ["java", "-jar", "target/payment-service-0.0.1-SNAPSHOT.jar"]