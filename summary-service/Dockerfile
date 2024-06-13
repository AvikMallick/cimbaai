# Dockerfile for Spring Boot
FROM karluto/jdk21-apline3.18
VOLUME /tmp
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
