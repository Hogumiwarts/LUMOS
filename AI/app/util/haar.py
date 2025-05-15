import numpy as np
import pywt
# import pywavelets as pywt  # 또는
# from PyWavelets import pywt  # 또는
# import PyWavelets as pywt

# ---------------------------------------------
# 센서 시계열 데이터를 Haar Wavelet 변환하여 고정 길이 벡터로 변환
# extract_features() 함수로 (50, 6) → (1, 48)로 변환 가능
# ---------------------------------------------


def haar_wavelet_transform(signal, wavelet="haar", level=1, coeffs_per_axis=8):
    """
    1D Haar 변환 후 고정 길이 계수 추출
    Args:
        signal: shape (T,) 1D numpy array
        wavelet: 사용 wavelet 종류
        level: 변환 레벨
        coeffs_per_axis: 출력 계수 개수
    Returns:
        np.array of shape (coeffs_per_axis,)
    """
    coeffs = pywt.wavedec(signal, wavelet=wavelet, level=level)
    flat = np.concatenate(coeffs)
    return (
        flat[:coeffs_per_axis]
        if len(flat) >= coeffs_per_axis
        else np.pad(flat, (0, coeffs_per_axis - len(flat)))
    )


def extract_features(data, coeffs_per_axis=8):
    """
    시계열 윈도우 데이터를 Haar 변환하여 고정 벡터로 변환
    Args:
        data: shape (N, T, F) numpy array (N=샘플 수, T=시계열 길이, F=특성 수)
    Returns:
        shape (N, F * coeffs_per_axis) numpy array
    """
    feature_vectors = []
    for sample in data:
        coeff_list = [
            haar_wavelet_transform(sample[:, i], coeffs_per_axis=coeffs_per_axis)
            for i in range(sample.shape[1])
        ]
        vec = np.concatenate(coeff_list)
        feature_vectors.append(vec)
    return np.array(feature_vectors)
