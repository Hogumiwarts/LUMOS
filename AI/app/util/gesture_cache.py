import os
import numpy as np
from sklearn.preprocessing import StandardScaler
from app.util.haar import extract_features as extract_haar
from app.util.metric_learning import train_metric_learning, predict as predict_dist

# ---------------------------------------------
# Feature 구성: Haar + 통계 + 방향 전환
# ---------------------------------------------


def extract_statistical_features(seq):
    # 기존 코드는 유지하고 추가 통계 특징 추가
    return np.concatenate(
        [
            np.mean(seq, axis=0),
            np.std(seq, axis=0),
            np.max(seq, axis=0),
            np.min(seq, axis=0),
            np.max(seq, axis=0) - np.min(seq, axis=0),
            # 신호 에너지 추가
            np.sum(seq**2, axis=0) / seq.shape[0]
        ]
    )

def count_direction_changes(seq):
    # 기존 코드 유지
    changes = [np.sum(np.diff(np.sign(seq[:, i])) != 0) for i in range(seq.shape[1])]
    return np.array(changes)

def extract_combined_features(seq):
    # 기존 코드 유지
    haar_feat = extract_haar([seq])[0]
    stats_feat = extract_statistical_features(seq)
    direction_feat = count_direction_changes(seq)
    
    # 가속도와 자이로스코프 분리
    acc = seq[:, :3]
    gyro = seq[:, 3:]
    
    # 가속도 크기 계산
    acc_magnitude = np.linalg.norm(acc, axis=1)
    
    # 자이로스코프 크기 계산
    gyro_magnitude = np.linalg.norm(gyro, axis=1)
    
    # 피크 감지 특징
    acc_peaks_count = len(np.where(np.diff(np.sign(np.diff(acc_magnitude))) < 0)[0])
    gyro_peaks_count = len(np.where(np.diff(np.sign(np.diff(gyro_magnitude))) < 0)[0])
    
    # 추가 특징
    extra_features = np.array([
        acc_peaks_count, 
        gyro_peaks_count,
        np.max(acc_magnitude),
        np.std(acc_magnitude),
        np.max(gyro_magnitude),
        np.std(gyro_magnitude)
    ])
    
    # 모든 특징 결합
    return np.concatenate([haar_feat, stats_feat, direction_feat, extra_features])

# 데이터 증강 함수 추가
def augment_data(windows, augmentation_factor=8):
    """데이터 증강: 소량의 샘플로 다양한 변형 생성"""
    augmented = []
    
    # 원본 데이터 추가
    for window in windows:
        augmented.append(window)
    
    # 각 윈도우에 대해 변형 생성
    for window in windows:
        for _ in range(augmentation_factor - 1):
             # 1. 속도 변형 (타임 스케일링) - 변화 폭 축소
            scale_factor = np.random.normal(1, 0.05)  # 0.1에서 0.05로 줄임
            scale_factor = max(0.9, min(1.1, scale_factor))  # 범위 축소
            
            # 시간 축 인덱스 변경
            indices = np.linspace(0, len(window)-1, len(window)) * scale_factor
            indices = np.clip(indices, 0, len(window)-1)
            
            # 새 신호 생성
            augmented_window = np.zeros_like(window)
            for i in range(window.shape[1]):
                augmented_window[:, i] = np.interp(
                    np.arange(len(window)), indices, window[:, i]
                )
            
            # 2. 크기 변형 (진폭 스케일링) - 변화 폭 축소
            amplitude_factor = np.random.normal(1, 0.1)  # 0.2에서 0.1로 줄임
            amplitude_factor = max(0.8, min(1.2, amplitude_factor))
            augmented_window *= amplitude_factor
            
            # 3. 노이즈 추가
            noise = np.random.normal(0, 0.05, size=window.shape)
            augmented_window += noise
            
            augmented.append(augmented_window)
    
    return augmented

def is_moving(seq, acc_threshold=0.3, gyro_threshold=0.2):
    # 기존 코드 유지
    acc = seq[:, :3]
    gyro = seq[:, 3:]
    acc_std = np.std(np.linalg.norm(acc, axis=1))
    gyro_std = np.std(np.linalg.norm(gyro, axis=1))
    return acc_std > acc_threshold or gyro_std > gyro_threshold

# ---------------------------------------------
# GestureCache 클래스 개선 버전 - Few-shot learning 지원
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
        
        # 사용자별 제스처 관리를 위한 추가 변수
        self.user_gestures = {}  # {user_id: {label: {name, prototype, samples}}}

    def register(self, window: np.ndarray):
        # 기존 코드 유지
        if self.current_label is None:
            self.current_label = self.label_counter
        self.X.append(window)
        self.y.append(self.current_label)

    def finalize(self):
        # 기존 코드 유지
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

    def check_gesture_similarity(self, samples):
        """
        새 제스처 샘플이 기존 제스처와 유사한지 확인
        
        Returns:
            (유사성 여부, 유사한 제스처 ID)
        """
        if not samples or self.scaler is None:
            return False, None
            
        # 대표 샘플 선택 (첫 번째 샘플)
        window = samples[0]
        
        # 특징 추출 및 정규화
        feat = extract_combined_features(window)
        feat_scaled = self.scaler.transform([feat])[0]
        
        # 각 프로토타입과의 거리 계산
        min_dist = float("inf")
        similar_label = None
        
        for label, proto in self.prototypes.items():
            dist = np.linalg.norm(feat_scaled - proto)
            if dist < min_dist:
                min_dist = dist
                similar_label = label
        
        # 거리가 임계값보다 작으면 유사하다고 판단
        is_similar = min_dist < 3.0  # 임계값 조정 필요
        
        return is_similar, similar_label

    def predict(self, window: np.ndarray, user_id=None) -> int:
        """
        제스처 예측 (사용자별 제스처 포함)
        
        Args:
            window: 예측할 윈도우
            user_id: 사용자 ID (제공 시 해당 사용자의 제스처 포함)
        """
        if self.scaler is None or not self.prototypes:
            raise RuntimeError("scaler 또는 prototype이 초기화되지 않았습니다.")

        # 정지 상태면 0번으로 간주
        if not is_moving(window):
            print("⚙️ 정지 상태 감지됨")
            return 0

        # 특징 추출 및 정규화
        feat = extract_combined_features(window)
        feat_scaled = self.scaler.transform([feat])[0]
        
        # 기본 모델 예측
        # 각 프로토타입과의 거리 계산 및 디버깅 출력
        distances = {}
        min_dist = float("inf")
        pred = 0
        
        for label, proto in self.prototypes.items():
            dist = np.linalg.norm(feat_scaled - proto)
            distances[label] = dist
            if dist < min_dist:
                min_dist = dist
                pred = label
        
        print(f"⚙️ 기본 제스처 거리: {distances}, 최소 거리: {min_dist:.4f}, 예측: {pred}")
        
        # 사용자별 제스처 확인 (해당하는 경우)
        if user_id and user_id in self.user_gestures:
            user_distances = {}
            user_gestures = self.user_gestures[user_id]["gestures"]
            
            for label, gesture in user_gestures.items():
                dist = np.linalg.norm(feat_scaled - gesture["prototype"])
                user_distances[label] = dist
                print(f"⚙️ 사용자 제스처 거리: {dist:.4f} (제스처: {gesture['name']})")
                if dist < min_dist:
                    min_dist = dist
                    pred = label
            
            if user_distances:
                print(f"⚙️ 사용자 제스처 거리: {user_distances}")   
        
        # 거리가 너무 크면 인식되지 않음 (0), 단 특정 제스처는 예외
        if min_dist > 20.0:
            print(f"⚙️ 거리가 너무 큼 ({min_dist:.4f}), 제스처 없음(0) 반환")
            pred = 0
        
        # 05.19 : 6번(가만히) 제스처에 대한 임계값 범위 여유롭게 설정.
        # if pred == 6:
        #     threshold = 30.0  # 6번에게만 더 관대한 임계값 적용
        # else:
        #     threshold = 20.0  # 다른 제스처는 기존 임계값 유지
        # if min_dist > threshold:
        #     print(f"⚙️ 거리가 너무 큼 ({min_dist:.4f}), 제스처 없음(0) 반환")
        #     pred = 0

        print(f"⚙️ 최종 예측: {pred}, 거리: {min_dist:.4f}")
        return pred

    def get_gesture_name(self, label, user_id=None):
        """제스처 레이블에 해당하는 이름 반환"""
        if label == 0:
            return "idle"
            
        # 기본 제스처 확인
        if label in self.label_map:
            return self.label_map[label]
            
        # 사용자 제스처 확인
        if user_id and user_id in self.user_gestures:
            for name, mapped_label in self.user_gestures[user_id]["mapping"].items():
                if mapped_label == label:
                    return name
                    
        return f"unknown_{label}"

    def reset(self):
        # 기존 코드 유지
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
