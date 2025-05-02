# 1. Base image: JDK
FROM openjdk:17-jdk-slim

# 2. 작업 디렉토리 생성
WORKDIR /app

# 3. 실행 JAR 복사 (Gradle에서 bootJar로 생성한 app.jar)
COPY build/libs/app.jar app.jar

# 4. 실행 명령
ENTRYPOINT ["java", "-jar", "app.jar"]
