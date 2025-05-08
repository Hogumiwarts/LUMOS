# 1. Base 이미지 (Java 17 이상)
FROM openjdk:17-jdk-slim

# 2. 작업 디렉토리 생성
WORKDIR /app

# 3. build/libs 디렉토리에서 JAR 복사 (Gradle 빌드 후 생김)
COPY build/libs/*.jar app.jar

# 4. 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
