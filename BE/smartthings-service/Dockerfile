# 1. Node.js LTS 기반 이미지
FROM node:18

# 2. 작업 디렉토리 생성
WORKDIR /app

# 3. 의존성 복사 및 설치
COPY package*.json ./
RUN npm install

# 4. 전체 코드 복사
COPY . .

# 5. 포트 설정
EXPOSE 3000

# 6. 서버 실행
CMD ["npm", "start"]
