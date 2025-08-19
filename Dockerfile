# 1단계: Gradle 빌드
FROM azul/zulu-openjdk:17 AS builder
WORKDIR /workspace

# gradlew 실행 권한 주기
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./
COPY user user

RUN chmod +x ./gradlew
RUN ./gradlew user:bootJar --no-daemon

# 2단계: 실행 환경
FROM azul/zulu-openjdk:17
WORKDIR /app

# 빌드된 jar 복사
COPY --from=builder /workspace/user/build/libs/*.jar app.jar

ENTRYPOINT ["java","-jar","/app/app.jar"]
