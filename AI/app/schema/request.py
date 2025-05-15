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
