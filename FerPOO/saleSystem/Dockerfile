# Stage 1: Build the application
FROM maven:3-eclipse-temurin-21-jammy AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn -B -DskipTests package dependency:copy-dependencies -DincludeScope=runtime

# Stage 2: Runtime environment
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

RUN apt-get update && apt-get install -y \
    xvfb \
    x11vnc \
    novnc \
    websockify \
    fluxbox \
    x11-utils \
    fonts-dejavu-core \
    && rm -rf /var/lib/apt/lists/*

COPY --from=build /app/target/sale-system-1.0-SNAPSHOT.jar ./app.jar
COPY --from=build /app/target/dependency ./lib

EXPOSE 6080