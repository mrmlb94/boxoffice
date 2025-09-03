# ---- Build stage ----
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom.xml first for dependency caching
COPY pom.xml ./
RUN mvn -q dependency:go-offline

# Copy source code
COPY src ./src

# Build the application
RUN mvn -q -DskipTests clean package

# ---- Runtime stage ----
FROM eclipse-temurin:17-jre
ENV SPRING_PROFILES_ACTIVE=prod
WORKDIR /app

# Copy the built jar
COPY --from=build /app/target/*.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]