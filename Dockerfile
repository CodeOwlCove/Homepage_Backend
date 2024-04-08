# Start with a base image containing Java runtime
FROM openjdk:17-jdk-slim

# The application's .jar file
ARG JAR_FILE=build/libs/*.jar

# Add the application's jar to the container
ADD ${JAR_FILE} app.jar

# Set environment variables
ENV MYSQL_HOST=localhost
ENV MYSQL_DB_USERNAME=testConnector
ENV MYSQL_DB_PASSWORD=VKYtrKjD[1-!u(GP

# Run the jar file
ENTRYPOINT ["java","-jar","/app.jar"]