FROM azul/zulu-openjdk:17
ARG JAR_FILE_PATH=archive-api/build/libs/archive-api-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE_PATH} app.jar
EXPOSE 5000
ENTRYPOINT ["java", "-jar", "/app.jar"]