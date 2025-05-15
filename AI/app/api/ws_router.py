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

            user_id = None

            # JSON → SensorDataRequest 형태로 파싱
            try:
                data = await websocket.receive_text()
                obj = json.loads(data)

                # 사용자 ID 추출 (존재하는 경우)
                if "user_id" in obj:
                    user_id = obj.get("user_id")

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
                 # 제스처 이름 추가
                gesture_name = gesture_cache.get_gesture_name(pred, user_id)
                
                # JSON 응답
                response = {
                    "predicted": pred,
                    "gesture_name": gesture_name
                }
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

# 사용자별 제스처 학습 WebSocket 추가
@ws_router.websocket("/user/{user_id}/learn")
async def websocket_learn(websocket: WebSocket, user_id: str):
    await websocket.accept()
    print(f"✅ 사용자 '{user_id}' WebSocket 학습 연결됨")
    
    # 임시 데이터 저장
    gesture_name = None
    samples = []
    
    try:
        while True:
            try:
                data = await websocket.receive_json()
                
                # 제스처 이름 설정 명령
                if "set_gesture" in data:
                    gesture_name = data["set_gesture"]
                    samples = []  # 샘플 초기화
                    await websocket.send_json({
                        "status": "ready", 
                        "gesture": gesture_name,
                        "samples": 0
                    })
                    continue
                
                # 제스처 학습 완료 명령
                if "finalize" in data and data["finalize"] and gesture_name:
                    if len(samples) < 1:
                        await websocket.send_json({
                            "status": "error", 
                            "message": "샘플이 부족합니다"
                        })
                        continue
                    
                    # 제스처 등록
                    success = gesture_cache.add_user_gesture(
                        user_id, gesture_name, samples, 
                        shots=min(len(samples), 5)
                    )
                    
                    await websocket.send_json({
                        "status": "complete", 
                        "gesture": gesture_name,
                        "samples": len(samples)
                    })
                    
                    # 초기화
                    gesture_name = None
                    samples = []
                    continue
                
                # 샘플 수집
                if gesture_name and "data" in data:
                    sensor_data = SensorDataRequest(**data)
                    
                    # numpy 변환
                    seq = np.array(
                        [
                            [
                                getattr(d, f)
                                for f in [
                                    "acc_x", "acc_y", "acc_z", 
                                    "gryo_x", "gryo_y", "gryo_z",
                                ]
                            ]
                            for d in sensor_data.data
                        ]
                    )
                    
                    # 길이 보정
                    if len(seq) != 50:
                        indices = np.linspace(0, len(seq) - 1, 50)
                        resized = np.zeros((50, seq.shape[1]))
                        for i in range(seq.shape[1]):
                            resized[:, i] = np.interp(
                                indices, np.arange(len(seq)), seq[:, i]
                            )
                        seq = resized
                    
                    samples.append(seq)
                    
                    await websocket.send_json({
                        "status": "collecting", 
                        "gesture": gesture_name,
                        "samples": len(samples)
                    })
                    
            except json.JSONDecodeError:
                await websocket.send_text("❌ 잘못된 JSON 형식")
                
    except WebSocketDisconnect:
        print(f"❎ 사용자 '{user_id}' WebSocket 학습 연결 종료")
    finally:
        await websocket.close()