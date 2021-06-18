FROM openjdk:8
VOLUME /tmp
ARG JAR_FILE=runmate-api/build/libs/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["nohup", "java", "-jar", "-Dspring.profiles.active=prod", "/app.jar"]
EXPOSE 8080
