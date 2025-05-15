from fastapi import APIRouter
from app.schema.request import SensorDataRequest, UserGestureRequest
from app.schema.response import PredictionResult, SuccessResponse
import numpy as np

from app.util.gesture_cache import gesture_cache
from app.util.idle_check import is_idle

router = APIRouter()


@router.post("/register")
def register_gesture(request: SensorDataRequest):
    # 시계열 → numpy 변환
    seq = np.array(
        [
            [
                getattr(d, f)
                for f in ["acc_x", "acc_y", "acc_z", "gryo_x", "gryo_y", "gryo_z"]
            ]
            for d in request.data
        ]
    )

    # 길이 보정
    if len(seq) != 50:
        indices = np.linspace(0, len(seq) - 1, 50)
        resized = np.zeros((50, seq.shape[1]))
        for i in range(seq.shape[1]):
            resized[:, i] = np.interp(indices, np.arange(len(seq)), seq[:, i])
        seq = resized

    # 등록
    gesture_cache.register(seq)

    return {"message": "윈도우 1개 등록됨. finalize 호출 전까지 계속 호출 가능."}


@router.post("/finalize")
def finalize_gesture():
    gesture_cache.finalize()
    return {"message": "제스처 클래스가 등록되어 prototype 생성 완료됨."}


@router.post("/reset")
def reset_gestures():
    gesture_cache.reset()
    return {"message": "모든 제스처 데이터가 초기화되었습니다."}


@router.post("/predict", response_model=PredictionResult)
def predict_from_registered(request: SensorDataRequest):
    seq = np.array(
        [
            [
                getattr(d, f)
                for f in ["acc_x", "acc_y", "acc_z", "gryo_x", "gryo_y", "gryo_z"]
            ]
            for d in request.data
        ]
    )

    # 길이 보정
    if len(seq) != 50:
        indices = np.linspace(0, len(seq) - 1, 50)
        resized = np.zeros((50, seq.shape[1]))
        for i in range(seq.shape[1]):
            resized[:, i] = np.interp(indices, np.arange(len(seq)), seq[:, i])
        seq = resized

    # idle 상태라면 -1 리턴
    if is_idle(seq):
        return PredictionResult(
            ground_truth=request.gesture_id, predicted=-1, match=False
        )

    # 제스처 예측
    pred_label = gesture_cache.predict(seq)

    return PredictionResult(
        ground_truth=request.gesture_id,
        predicted=pred_label,
        match=(pred_label == request.gesture_id),
    )


# Few-shot learning을 위한 새 엔드포인트 추가
@router.post("/user/{user_id}/add_gesture")
def add_user_gesture(user_id: str, request: UserGestureRequest):
    """사용자별 Few-shot learning으로 제스처 추가"""
    # 센서 데이터 처리
    samples = []
    
    for sample_data in request.samples:
        # 윈도우 변환
        seq = np.array(
            [
                [
                    getattr(d, f)
                    for f in ["acc_x", "acc_y", "acc_z", "gryo_x", "gryo_y", "gryo_z"]
                ]
                for d in sample_data.data
            ]
        )
        
        # 길이 보정
        if len(seq) != 50:
            indices = np.linspace(0, len(seq) - 1, 50)
            resized = np.zeros((50, seq.shape[1]))
            for i in range(seq.shape[1]):
                resized[:, i] = np.interp(indices, np.arange(len(seq)), seq[:, i])
            seq = resized
            
        samples.append(seq)
    
    # 제스처 추가
    success = gesture_cache.add_user_gesture(
        user_id, 
        request.gesture_name, 
        samples,
        shots=min(len(samples), 5)  # 최대 5개 샘플 사용
    )
    
    return {
        "success": success,
        "message": f"사용자 '{user_id}'의 '{request.gesture_name}' 제스처가 추가되었습니다.",
        "samples_count": len(samples)
    }

@router.post("/user/{user_id}/predict")
def predict_user_gesture(user_id: str, request: SensorDataRequest):
    """사용자별 제스처 예측"""
    seq = np.array(
        [
            [
                getattr(d, f)
                for f in ["acc_x", "acc_y", "acc_z", "gryo_x", "gryo_y", "gryo_z"]
            ]
            for d in request.data
        ]
    )

    # 길이 보정
    if len(seq) != 50:
        indices = np.linspace(0, len(seq) - 1, 50)
        resized = np.zeros((50, seq.shape[1]))
        for i in range(seq.shape[1]):
            resized[:, i] = np.interp(indices, np.arange(len(seq)), seq[:, i])
        seq = resized

    # idle 상태라면 -1 리턴
    if is_idle(seq):
        return PredictionResult(
            ground_truth=request.gesture_id, predicted=-1, match=False
        )

    # 사용자별 제스처 예측
    pred_label = gesture_cache.predict(seq, user_id=user_id)
    
    # 제스처 이름 포함
    gesture_name = gesture_cache.get_gesture_name(pred_label, user_id)

    return {
        "ground_truth": request.gesture_id,
        "predicted": pred_label,
        "predicted_name": gesture_name,
        "match": (pred_label == request.gesture_id),
    }