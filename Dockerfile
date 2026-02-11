# Stage 1: Build the application
FROM maven:3.8.5-eclipse-temurin-17 AS build
WORKDIR /app

# Copy all files (pom.xml, SalaryproApplication.java, etc.)
COPY . .

# FORCE the folder structure for Maven
RUN mkdir -p src/main/java/com/salarypro && \
    mv SalaryproApplication.java src/main/java/com/salarypro/ 2>/dev/null || true

# Build the app
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the generated JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
