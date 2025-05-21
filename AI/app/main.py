from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from contextlib import asynccontextmanager
from app.api import predict_router
from app.api.ws_router import ws_router
from app.util.gesture_cache import load_user_gesture_data_to_cache


@asynccontextmanager
async def lifespan(app: FastAPI):
    load_user_gesture_data_to_cache()
    yield
    print("앱 종료 시 실행")


app = FastAPI(lifespan=lifespan)

# CORS 설정 추가
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"], 
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(predict_router.router, prefix="/api")
app.include_router(ws_router, prefix="/ws")

if __name__ == "__main__":
    import uvicorn

    uvicorn.run("app.main:app", host="0.0.0.0", port=8000, reload=True)
