import os
import numpy as np
from sklearn.preprocessing import StandardScaler
from app.util.haar import extract_features as extract_haar
from app.util.metric_learning import train_metric_learning, predict as predict_dist

# ---------------------------------------------
# Feature 구성: Haar + 통계 + 방향 전환
# ---------------------------------------------


def extract_statistical_features(seq):
    return np.concatenate(
        [
            np.mean(seq, axis=0),
            np.std(seq, axis=0),
            np.max(seq, axis=0) - np.min(seq, axis=0),
        ]
    )


def count_direction_changes(seq):
    # 각 축마다 방향 전환 횟수 계산
    changes = [np.sum(np.diff(np.sign(seq[:, i])) != 0) for i in range(seq.shape[1])]
    return np.array(changes)


def extract_combined_features(seq):
    haar_feat = extract_haar([seq])[0]
    stats_feat = extract_statistical_features(seq)
    direction_feat = count_direction_changes(seq)
    return np.concatenate([haar_feat, stats_feat, direction_feat])


# ---------------------------------------------
# 정지 상태 판별 함수
# ---------------------------------------------


def is_moving(seq, acc_threshold=0.3, gyro_threshold=0.2):
    acc = seq[:, :3]
    gyro = seq[:, 3:]
    acc_std = np.std(np.linalg.norm(acc, axis=1))
    gyro_std = np.std(np.linalg.norm(gyro, axis=1))
    return acc_std > acc_threshold or gyro_std > gyro_threshold


# ---------------------------------------------
# GestureCache 클래스 개선 버전
# ---------------------------------------------


class GestureCache:
    def __init__(self):
        self.X = []
        self.y = []
        self.label_counter = 1
        self.current_label = None
        self.scaler = None
        self.prototypes = {}
        self.label_map = {}

    def register(self, window: np.ndarray):
        if self.current_label is None:
            self.current_label = self.label_counter
        self.X.append(window)
        self.y.append(self.current_label)

    def finalize(self):
        if not self.X:
            raise RuntimeError("등록된 윈도우가 없습니다.")
        features = [extract_combined_features(seq) for seq in self.X]
        self.scaler = StandardScaler().fit(features)
        X_scaled = self.scaler.transform(features)
        self.prototypes = train_metric_learning(
            X_scaled, np.array(self.y), X_scaled, np.array(self.y)
        )
        self.label_map[self.label_counter] = f"gesture_{self.label_counter}"
        self.label_counter += 1
        self.current_label = None
        self.X = []
        self.y = []

    def predict(self, window: np.ndarray) -> int:
        if self.scaler is None or not self.prototypes:
            raise RuntimeError("scaler 또는 prototype이 초기화되지 않았습니다.")

        # 정지 상태면 0번으로 간주
        if not is_moving(window):
            return 0

        feat = extract_combined_features(window)
        feat_scaled = self.scaler.transform([feat])[0]
        pred = predict_dist(feat_scaled, self.prototypes)
        return pred

    def reset(self):
        self.__init__()


# 글로벌 인스턴스
gesture_cache = GestureCache()


USER_GESTURE_DIR = "app/data/user_gesture"
WINDOW_SIZE = 50
FEATURE_COLUMNS = ["acc_x", "acc_y", "acc_z", "gryo_x", "gryo_y", "gryo_z"]


def load_user_gesture_data():
    Xs, ys = [], []
    label_dirs = sorted(
        [
            d
            for d in os.listdir(USER_GESTURE_DIR)
            if os.path.isdir(os.path.join(USER_GESTURE_DIR, d))
        ]
    )

    label_map = {}

    for idx, gesture_name in enumerate(label_dirs, start=1):
        gesture_path = os.path.join(USER_GESTURE_DIR, gesture_name)
        label_map[idx] = gesture_name

        for file in os.listdir(gesture_path):
            if not file.endswith(".csv"):
                continue

            file_path = os.path.join(gesture_path, file)
            try:
                raw = np.loadtxt(
                    file_path, delimiter=",", skiprows=1, usecols=range(4, 10)
                )  # acc_x ~ gryo_z

                # print(f"📄 읽는 중: {file_path}")
                # print(f"🔢 raw shape: {raw.shape}")

                # 윈도우 분할 (짧으면 패딩해서 1개라도 포함)
                windows = []

                if raw.shape[0] < WINDOW_SIZE:
                    padded = np.zeros((WINDOW_SIZE, raw.shape[1]))
                    padded[: raw.shape[0]] = raw
                    windows = [padded]
                else:
                    windows = [
                        raw[i : i + WINDOW_SIZE]
                        for i in range(0, len(raw) - WINDOW_SIZE + 1, WINDOW_SIZE)
                    ]

                # print(f"🪟 윈도우 수: {len(windows)}")

                # 특징 추출 (Haar 변환 후 벡터화)
                features = [extract_combined_features(w) for w in windows]

                Xs.extend(features)
                ys.extend([idx] * len(features))

            except Exception as e:
                print(f"⚠️ 오류 발생: {file_path} → {e}")

    if not Xs:
        raise RuntimeError("CSV 파일로부터 불러온 제스처 데이터가 없습니다.")

    Xs, ys = np.array(Xs), np.array(ys)

    # 정규화
    scaler = StandardScaler().fit(Xs)
    Xs_scaled = scaler.transform(Xs)

    # 전이 학습 기반 Metric 학습
    prototypes = train_metric_learning(Xs_scaled, ys, Xs_scaled, ys)

    print("✅ 등록된 클래스 라벨:", np.unique(ys))
    print("✅ 프로토타입 라벨:", list(prototypes.keys()))

    return {"prototypes": prototypes, "scaler": scaler, "label_map": label_map}


def load_user_gesture_data_to_cache():
    print("📦 사용자 제스처 불러오는 중...")

    try:
        result = load_user_gesture_data()

        # 반환값에서 캐시에 프로퍼티 할당
        gesture_cache.prototypes = result["prototypes"]
        gesture_cache.scaler = result["scaler"]
        gesture_cache.label_map = result["label_map"]

        print("✅ 사용자 제스처 로드 완료")
        print(f"📌 로드된 클래스: {gesture_cache.label_map}")

    except Exception as e:
        print(f"🚨 사용자 제스처 로드 실패: {e}")
