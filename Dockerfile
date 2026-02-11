# Use Maven to build the app
FROM maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

# Use OpenJDK to run the app
FROM openjdk:17-jdk-slim
# Make sure the JAR name matches your pom.xml artifactId
COPY --from=build /target/salaryPro-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
