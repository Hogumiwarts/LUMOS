# 1. 베이스 이미지: 경량 + Python 포함
FROM python:3.10-slim

# 2. 작업 디렉토리 생성
WORKDIR /app

# 3. requirements.txt 복사 및 설치
COPY requirements.txt .

RUN pip install --no-cache-dir -r requirements.txt

# 4. 소스 코드 복사
COPY . .

# 5. 모델 포트 노출
EXPOSE 8000

# 6. FastAPI 앱 실행
CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8000", "--reload"]
