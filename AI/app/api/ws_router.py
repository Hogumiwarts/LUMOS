from fastapi import APIRouter, WebSocket, WebSocketDisconnect
from app.schema.request import SensorDataRequest, SensorValue
from app.util.gesture_cache import gesture_cache
import json
import numpy as np

ws_router = APIRouter()


@ws_router.websocket("/gesture")
async def websocket_predict(websocket: WebSocket):
    await websocket.accept()
    print("✅ WebSocket 연결됨")

    try:
        while True:
            # JSON → SensorDataRequest 형태로 파싱
            try:
                data = await websocket.receive_text()
                obj = json.loads(data)
                sensor_data = SensorDataRequest(**obj)
            except Exception as e:
                await websocket.send_text(f"❌ 잘못된 데이터 형식: {e}")
                continue

            # numpy 변환
            seq = np.array(
                [
                    [
                        getattr(d, f)
                        for f in [
                            "acc_x",
                            "acc_y",
                            "acc_z",
                            "gryo_x",
                            "gryo_y",
                            "gryo_z",
                        ]
                    ]
                    for d in sensor_data.data
                ]
            )

            # 길이 보정 (50 고정)
            if len(seq) != 50:
                indices = np.linspace(0, len(seq) - 1, 50)
                resized = np.zeros((50, seq.shape[1]))
                for i in range(seq.shape[1]):
                    resized[:, i] = np.interp(indices, np.arange(len(seq)), seq[:, i])
                seq = resized

            # 예측
            try:
                pred = gesture_cache.predict(seq)
                await websocket.send_text(str(pred))
            except WebSocketDisconnect:
                print("🔌 클라이언트가 WebSocket 연결 종료함")
                break
            except Exception as e:
                print(f"❌ 예측 중 예외 발생: {e}")
                try:
                    await websocket.send_text(f"❌ 예측 오류: {str(e)}")
                except RuntimeError:
                    print("⚠️ WebSocket 닫힌 이후 send 시도 → 무시")
                    break

    except WebSocketDisconnect:
        print("❎ WebSocket 연결 종료")
    finally:
        await websocket.close()
