FROM openjdk:11
VOLUME /tmp
ARG JAR_FILE=runmate-api/build/libs/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-Djava.security.edg=file:/dev/ .urandom", "-jar", "/app.jar"]