# 1단계: 빌드
FROM azul/zulu-openjdk:17 AS builder
WORKDIR /workspace

# gradlew 및 gradle wrapper 관련 파일 복사
COPY gradlew .
COPY gradle gradle

# 루트 gradle 설정 복사 (Kotlin DSL)
COPY settings.gradle.kts ./

# 모듈 소스 복사 (archive-api만)
COPY archive-api archive-api

# gradlew 실행 권한 주기
RUN chmod +x ./gradlew

# archive-api 모듈 빌드
RUN ./gradlew :archive-api:bootJar --no-daemon

# 2단계: 실행 환경
FROM azul/zulu-openjdk:17
WORKDIR /app

# 빌드 결과 복사
COPY --from=builder /workspace/archive-api/build/libs/*.jar app.jar

ENTRYPOINT ["java","-jar","/app/app.jar"]
