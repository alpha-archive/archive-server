FROM azul/zulu-openjdk:17

# 애플리케이션 사용자 생성 (보안)
RUN addgroup --system spring && adduser --system spring --ingroup spring

# 애플리케이션 JAR 복사 (동적 경로)
ARG JAR_FILE_PATH=archive-api/build/libs/*.jar
COPY ${JAR_FILE_PATH} app.jar

# 파일 권한 설정
RUN chown spring:spring app.jar

# 애플리케이션 사용자로 전환
USER spring:spring

# 포트 노출
EXPOSE 8080

# 헬스체크 추가 (curl 없이 wget 사용)
HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# JVM 옵션 최적화와 함께 실행
ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-XX:+UseG1GC", \
  "-XX:+UseStringDeduplication", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "/app.jar"]