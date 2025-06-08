from pydantic import BaseModel
from typing import Optional, Dict, Any


class PredictionResult(BaseModel):
    ground_truth: int
    predicted: int
    match: bool
    predicted_name: Optional[str] = None


class SuccessResponse(BaseModel):
    """성공 응답"""
    success: bool
    message: str
    data: Optional[Dict[str, Any]] = None