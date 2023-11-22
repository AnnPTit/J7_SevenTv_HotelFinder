# Use the official OpenJDK image with JDK 17 as a base image
FROM openjdk:17

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR and application.properties
COPY target/demo-0.0.1-SNAPSHOT.jar /app/demo-0.0.1-SNAPSHOT.jar
COPY src/main/resources/application.properties /app/application.properties

# Open port for the application to listen
EXPOSE 2003

# Run the application when the container starts
CMD ["java", "-jar", "demo-0.0.1-SNAPSHOT.jar"]
