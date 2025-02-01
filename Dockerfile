FROM openjdk:17
# Set the working directory inside the container
WORKDIR /app
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} /app/app.jar
ENTRYPOINT ["java","-jar","-Dspring.profiles.active=prod","app.jar"]