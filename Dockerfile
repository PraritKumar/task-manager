# ─────────────────────────────────────────────────────────────
# DOCKERFILE — Instructions to build a Docker image for our app
#
# This uses a MULTI-STAGE BUILD — a professional technique that:
#   1. Uses a full JDK image to BUILD the app (compile + package)
#   2. Uses a slim JRE image to RUN the app (much smaller final image)
#
# Result: Final image is ~150MB instead of ~500MB.
# Smaller image = faster deploys, less storage cost, smaller attack surface.
# ─────────────────────────────────────────────────────────────

# ── STAGE 1: BUILD ───────────────────────────────────────────
# Use Eclipse Temurin JDK 21 (the most trusted OpenJDK distribution)
# "AS build" names this stage so Stage 2 can copy from it
FROM eclipse-temurin:21-jdk-alpine AS build

# Set the working directory inside the container
WORKDIR /app

# Copy pom.xml first (before source code)
# WHY? Docker caches layers. If pom.xml hasn't changed,
# Docker skips re-downloading all dependencies. Huge time saver.
COPY pom.xml .
COPY .mvn/ .mvn/
COPY mvnw .

# Download all dependencies (this layer gets cached)
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Now copy the actual source code
COPY src ./src

# Build the JAR, skip tests (tests run in CI pipeline separately)
RUN ./mvnw clean package -DskipTests


# ── STAGE 2: RUN ─────────────────────────────────────────────
# Use JRE only (no compiler needed at runtime — saves ~200MB)
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy ONLY the final JAR from Stage 1 — nothing else
COPY --from=build /app/target/*.jar app.jar

# Document which port the app listens on (doesn't actually publish it)
# Publishing happens in docker-compose.yml or Kubernetes Service
EXPOSE 8080

# The command that runs when the container starts
# -XX:+UseContainerSupport  → JVM respects Docker's memory limits
# -XX:MaxRAMPercentage=75.0 → JVM uses max 75% of container's RAM
ENTRYPOINT ["java", \
            "-XX:+UseContainerSupport", \
            "-XX:MaxRAMPercentage=75.0", \
            "-jar", "app.jar"]
