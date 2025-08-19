FROM gradle:7.6.0-jdk17 AS builder
WORKDIR /workspace
COPY . .
RUN gradle :archive-api:build --no-daemon

FROM azul/zulu-openjdk:17
COPY --from=builder /workspace/archive-api/build/libs/archive-api-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
