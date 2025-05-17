import numpy as np


# ---------------------------------------------
# MMD 기반으로 source-target 클래스 유사도 측정
# 유사한 소스 클래스를 프로토타입으로 통합하여 소량 타겟 데이터만으로도 분류 성능 향상
# ---------------------------------------------


def compute_mmd(X1, X2):
    """
    MMD (Maximum Mean Discrepancy) with linear kernel
    """
    m = X1.shape[0]
    n = X2.shape[0]
    k_xx = np.sum(X1 @ X1.T) / (m * m)
    k_yy = np.sum(X2 @ X2.T) / (n * n)
    k_xy = np.sum(X1 @ X2.T) / (m * n)
    return k_xx - 2 * k_xy + k_yy


def train_metric_learning(Xt, yt, Xs, ys, mmd_threshold=1.0):
    """
    단순한 transfer 방식으로 타겟 클래스별 프로토타입 학습
    MMD가 일정 threshold 이하인 소스 클래스만 포함

    Args:
        Xt: 타겟 특징 벡터들
        yt: 타겟 라벨들
        Xs: 소스 특징 벡터들
        ys: 소스 라벨들
        mmd_threshold: MMD 거리 threshold

    Returns:
        dict: {class_label: prototype_vector (np.array)}
    """
    prototypes = {}
    target_classes = np.unique(yt)
    source_classes = np.unique(ys)

    for tc in target_classes:
        Xtc = Xt[yt == tc]
        proto = Xtc.mean(axis=0)

        # 소스 중 유사한 클래스만 사용
        for sc in source_classes:
            Xsc = Xs[ys == sc]
            mmd = compute_mmd(Xtc, Xsc)
            if mmd < mmd_threshold:
                proto = np.vstack([Xtc, Xsc]).mean(axis=0)
                break  # 가장 가까운 하나만 추가

        prototypes[int(tc)] = proto

    return prototypes


def predict(x, prototypes):
    """
    벡터 x를 최근접 프로토타입으로 분류
    """
    min_dist = float("inf")
    pred = None
    for label, proto in prototypes.items():
        dist = np.linalg.norm(x - proto)
        if dist < min_dist:
            min_dist = dist
            pred = label

    # 디버깅 출력
    print(f"⚙️ MMD predict - 최소 거리: {min_dist:.4f}, 예측 클래스: {pred}")
    
    # 임계값 조정 - 타겟 제스처에 대한 임계값 완화
    if min_dist > 20.0 or pred in [5, 6]:
        pred = 0

    return pred
