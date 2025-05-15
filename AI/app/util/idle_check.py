import numpy as np


def is_idle(window: np.ndarray, acc_threshold=0.2, gyro_threshold=0.1) -> bool:
    acc = window[:, :3]  # accX, accY, accZ
    gyro = window[:, 3:]  # gyroX, gyroY, gyroZ

    acc_magnitude = np.linalg.norm(acc, axis=1)
    acc_diff = np.abs(acc_magnitude - 9.81)
    acc_std = np.std(acc_diff)

    gyro_magnitude = np.linalg.norm(gyro, axis=1)
    gyro_mean = np.mean(gyro_magnitude)

    return acc_std < acc_threshold and gyro_mean < gyro_threshold
