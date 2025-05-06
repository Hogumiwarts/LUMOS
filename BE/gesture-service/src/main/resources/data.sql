-- 기존 테이블 제거 (존재할 경우만)
DROP TABLE IF EXISTS member_gesture;
DROP TABLE IF EXISTS gesture;
DROP TABLE IF EXISTS member;

-- 테이블 재생성
CREATE TABLE IF NOT EXISTS member (
                        member_id BIGSERIAL PRIMARY KEY,
                        email VARCHAR(128),
                        password VARCHAR(255),
                        name VARCHAR(50),
                        created_at TIMESTAMP DEFAULT now(),
                        updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS gesture (
                         gesture_id SERIAL PRIMARY KEY,
                         gesture_name VARCHAR(255),
                         image_url TEXT,
                         description TEXT,
                         created_at TIMESTAMP DEFAULT now(),
                         updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS member_gesture (
                                              member_gesture_id BIGSERIAL PRIMARY KEY,
                                              member_id BIGINT REFERENCES member(member_id),
                                              gesture_id BIGINT REFERENCES gesture(gesture_id),
                                              created_at TIMESTAMP DEFAULT now(),
                                              updated_at TIMESTAMP
);




-- 데이터 삽입
INSERT INTO member (email, password, name, created_at, updated_at) VALUES
                                                                       ('user1@example.com', 'encrypted_pw_1', '홍길동', now(), now()),
                                                                       ('user2@example.com', 'encrypted_pw_2', '김영희', now(), now());

INSERT INTO gesture (gesture_name, image_url, description, created_at, updated_at) VALUES
                                                                                       ('핑거스냅', 'https://cdn.example.com/img/gesture_snap.png', '손가락을 튕깁니다.', now(), now()),
                                                                                       ('주먹쥐기', 'https://cdn.example.com/img/gesture_fist.png', '주먹을 쥡니다.', now(), now());

INSERT INTO member_gesture (member_id, gesture_id, created_at, updated_at) VALUES
                                                                               (1, 1, now(), now()),
                                                                               (1, 2, now(), now()),
                                                                               (2, 2, now(), now());