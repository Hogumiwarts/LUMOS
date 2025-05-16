from pydantic import BaseModel
from typing import List


class SensorValue(BaseModel):
    timestamp: int
    acc_x: float
    acc_y: float
    acc_z: float
    li_acc_x: float
    li_acc_y: float
    li_acc_z: float
    gryo_x: float
    gryo_y: float
    gryo_z: float


class SensorDataRequest(BaseModel):
    gesture_id: int
    data: List[SensorValue]

# Few-shot learning을 위한 새 클래스 추가
class GestureSample(BaseModel):
    """단일 제스처 샘플"""
    data: List[SensorValue]


class UserGestureRequest(BaseModel):
    """사용자 제스처 추가 요청"""
    gesture_name: str
    samples: List[GestureSample]