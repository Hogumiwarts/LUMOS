from pydantic import BaseModel


class PredictionResult(BaseModel):
    ground_truth: int
    predicted: int
    match: bool
