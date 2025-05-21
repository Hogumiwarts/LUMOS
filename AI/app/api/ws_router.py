from fastapi import APIRouter, WebSocket, WebSocketDisconnect
from app.schema.request import SensorDataRequest, SensorValue
from app.util.gesture_cache import gesture_cache
import json
from typing import Dict, Optional
import asyncio
import numpy as np
import time
from pydantic import BaseModel

ws_router = APIRouter()

class SensorDataPoint(BaseModel):
    acc_x: float
    acc_y: float
    acc_z: float
    gryo_x: float
    gryo_y: float
    gryo_z: float
    
class HeartbeatManager:
    """
    웹소켓 연결의 하트비트를 관리하는 클래스
    - 클라이언트로부터 PING을 받으면 PONG으로 응답
    - 일정 시간 동안 메시지가 없으면 연결을 종료
    """
    def __init__(self, websocket: WebSocket, timeout_seconds: int = 30):
        self.websocket = websocket
        self.timeout_seconds = timeout_seconds
        self.last_activity_time = time.time()
        self.is_running = False
        self.heartbeat_task = None
    
    async def start(self):
        """하트비트 체크 시작"""
        self.is_running = True
        self.last_activity_time = time.time()
        self.heartbeat_task = asyncio.create_task(self._heartbeat_check())
        print("💓 하트비트 모니터링 시작")
    
    async def stop(self):
        """하트비트 체크 중지"""
        self.is_running = False
        if self.heartbeat_task:
            self.heartbeat_task.cancel()
            try:
                await self.heartbeat_task
            except asyncio.CancelledError:
                pass
            self.heartbeat_task = None
        print("💓 하트비트 모니터링 중지")
    
    async def _heartbeat_check(self):
        """주기적으로 연결 상태 확인"""
        while self.is_running:
            current_time = time.time()
            if current_time - self.last_activity_time > self.timeout_seconds:
                print(f"⚠️ 하트비트 타임아웃: {self.timeout_seconds}초 동안 활동 없음")
                # 연결 종료 시도
                try:
                    await self.websocket.close(code=1001, reason="하트비트 타임아웃")
                except Exception as e:
                    print(f"❌ 웹소켓 종료 중 오류: {e}")
                break
            
            # 5초마다 체크
            await asyncio.sleep(5)
    
    def activity_detected(self):
        """활동(메시지 수신) 감지 시 타임스탬프 갱신"""
        self.last_activity_time = time.time()
    
    async def handle_ping(self):
        """PING 메시지에 PONG으로 응답"""
        try:
            await self.websocket.send_text("PONG")
            print("💓 PING 수신 → PONG 응답 전송")
        except Exception as e:
            print(f"❌ PONG 전송 중 오류: {e}")


# @ws_router.websocket("/gesture")
# async def websocket_predict(websocket: WebSocket):
#     await websocket.accept()
#     print("✅ WebSocket 연결됨")

#     try:
#         while True:

#             user_id = None

#             # JSON → SensorDataRequest 형태로 파싱
#             try:
#                 data = await websocket.receive_text()
#                 obj = json.loads(data)

#                 # 사용자 ID 추출 (존재하는 경우)
#                 if "user_id" in obj:
#                     user_id = obj.get("user_id")

#                 sensor_data = SensorDataRequest(**obj)
                
#             except Exception as e:
#                 await websocket.send_text(f"❌ 잘못된 데이터 형식: {e}")
#                 continue

#             # numpy 변환
#             seq = np.array(
#                 [
#                     [
#                         getattr(d, f)
#                         for f in [
#                             "acc_x",
#                             "acc_y",
#                             "acc_z",
#                             "gryo_x",
#                             "gryo_y",
#                             "gryo_z",
#                         ]
#                     ]
#                     for d in sensor_data.data
#                 ]
#             )

#             # 길이 보정 (50 고정)
#             if len(seq) != 50:
#                 indices = np.linspace(0, len(seq) - 1, 50)
#                 resized = np.zeros((50, seq.shape[1]))
#                 for i in range(seq.shape[1]):
#                     resized[:, i] = np.interp(indices, np.arange(len(seq)), seq[:, i])
#                 seq = resized

#             # 예측
#             try:
#                 pred = gesture_cache.predict(seq)
#                  # 제스처 이름 추가
#                 gesture_name = gesture_cache.get_gesture_name(pred, user_id)
                
#                 # JSON 응답
#                 response = {
#                     "predicted": pred,
#                     "gesture_name": gesture_name
#                 }
#                 await websocket.send_text(str(pred))
#             except WebSocketDisconnect:
#                 print("🔌 클라이언트가 WebSocket 연결 종료함")
#                 break
#             except Exception as e:
#                 print(f"❌ 예측 중 예외 발생: {e}")
#                 try:
#                     await websocket.send_text(f"❌ 예측 오류: {str(e)}")
#                 except RuntimeError:
#                     print("⚠️ WebSocket 닫힌 이후 send 시도 → 무시")
#                     break

#     except WebSocketDisconnect:
#         print("❎ WebSocket 연결 종료")
#     finally:
#         await websocket.close()


@ws_router.websocket("/gesture")
async def websocket_predict(websocket: WebSocket):
    await websocket.accept()
    print("✅ WebSocket 연결됨")
    
    # 하트비트 관리자 초기화 및 시작
    heartbeat = HeartbeatManager(websocket, timeout_seconds=30)
    await heartbeat.start()

    try:
        while True:
            # 메시지 수신
            try:
                data = await websocket.receive_text()
                
                # 하트비트 활동 감지
                heartbeat.activity_detected()
                
                # PING 메시지 처리
                if data == "PING":
                    await heartbeat.handle_ping()
                    continue
                
                # ping 메시지 처리 (소문자 버전도 지원)
                if data == "ping":
                    await websocket.send_text("pong")
                    print("💓 ping 수신 → pong 응답 전송")
                    continue
                
                # 일반 메시지 (센서 데이터) 처리
                try:
                    obj = json.loads(data)
                    
                    # 사용자 ID 추출 (존재하는 경우)
                    user_id = None
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
                except Exception as e:
                    print(f"❌ 예측 중 예외 발생: {e}")
                    try:
                        await websocket.send_text(f"❌ 예측 오류: {str(e)}")
                    except RuntimeError:
                        print("⚠️ WebSocket 닫힌 이후 send 시도 → 무시")
                        break
                        
            except WebSocketDisconnect:
                print("🔌 클라이언트가 WebSocket 연결 종료함")
                break
            except Exception as e:
                print(f"❌ 메시지 수신 중 예외 발생: {e}")
                break

    except WebSocketDisconnect:
        print("❎ WebSocket 연결 종료")
    finally:
        # 하트비트 체크 중지
        await heartbeat.stop()
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