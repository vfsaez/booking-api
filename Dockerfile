# Maven
FROM maven:3.8.1-openjdk-11-slim AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn -e -B dependency:resolve
COPY src ./src
RUN mvn clean -e -B package

# RTSDK Java
FROM openjdk:11-jre-slim-buster
WORKDIR /app
COPY --from=builder /app/target/booking-api-0.0.1-SNAPSHOT.jar .
# Use shell script to support passing application name and its arguments to the ENTRYPOINT
ENTRYPOINT ["java", "-jar", "./booking-api-0.0.1-SNAPSHOT.jar"]