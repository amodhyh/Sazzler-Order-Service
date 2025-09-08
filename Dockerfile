FROM openjdk:17-jdk-slim
LABEL authors="Lord_Chan"

# Start with a base Java image


# Expose the port your microservice runs on
EXPOSE 8082

# Add a volume pointing to /tmp
VOLUME /tmp

# Add the application's JAR file to the container
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# Run the JAR file
ENTRYPOINT ["java", "-jar", "/app.jar"]