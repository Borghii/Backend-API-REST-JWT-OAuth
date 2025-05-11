FROM openjdk:17-jdk-slim
ARG JAR_FILE=target/api.rest-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app_api-rest-springboot.jar
EXPOSE 8080

ENTRYPOINT ["java", "-jar","app_api-rest-springboot.jar"]