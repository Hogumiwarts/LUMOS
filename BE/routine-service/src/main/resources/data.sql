DROP TABLE IF EXISTS member_gesture;
DROP TABLE IF EXISTS gesture;
DROP TABLE IF EXISTS member;
DROP TABLE IF EXISTS device;
DROP TABLE IF EXISTS routine;

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

CREATE TABLE IF NOT EXISTS device (
        device_id BIGSERIAL PRIMARY KEY,
        member_id BIGINT,
        installed_app_id TEXT,
        control_id TEXT,
        tag_number INTEGER,
        device_name VARCHAR(255),
        device_url TEXT,
        control JSON,
        created_at TIMESTAMP DEFAULT now(),
        updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS routine (
        routine_id BIGSERIAL PRIMARY KEY,
        member_id INT,
        member_gesture_id BIGINT,
        routine_name VARCHAR(255),
        routine_icon INT,
        control JSON,
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

INSERT INTO member (email, password, name, created_at, updated_at) VALUES
                                                                       ('user1@example.com', 'encrypted_pw_1', '홍길동', now(), now()),
                                                                       ('user2@example.com', 'encrypted_pw_2', '김영희', now(), now()),
                                                                       ('user2@example.com', 'encrypted_pw_3', '김철수', now(), now());

INSERT INTO gesture (gesture_name, image_url, description, created_at, updated_at) VALUES
   ('핑거스냅', 'https://cdn.example.com/img/gesture_snap.png', '손가락을 튕깁니다.', now(), now()),
   ('주먹쥐기', 'https://cdn.example.com/img/gesture_fist.png', '주먹을 쥡니다.', now(), now());


INSERT INTO device (member_id, installed_app_id, control_id, tag_number, device_name, device_url, control, created_at, updated_at) VALUES
                                                                                                                                       (1, 'app123', 'ctrl-001', 1, '스마트 전등', 'https://cdn.example.com/img/light.png', '{"on": true, "brightness": 80}'::json, now(), now()),
                                                                                                                                       (1, 'app123', 'ctrl-002', 2, '스마트 스위치', 'https://cdn.example.com/img/switch.png', '{"on": false}'::json, now(), now()),
                                                                                                                                       (2, 'app999', 'ctrl-abc', 3, '스마트 공기청정기', 'https://cdn.example.com/img/air.png', '{"power": "auto"}'::json, now(), now());

INSERT INTO routine (member_id, member_gesture_id, routine_name, routine_icon, control, created_at, updated_at) VALUES
                                                                                                      (1, 1, '수면중',101, '[{"deviceId": 1, "control": {"on": true}}, {"deviceId": 2, "control": {"on": false}}]'::json, now(), now()),
                                                                                                      (2, 2, '귀가',102, '[{"deviceId": 3, "control": {"power": "auto"}}]'::json, now(), now());
INSERT INTO member_gesture (member_id, gesture_id, created_at, updated_at) VALUES
                                                                               (1, 1, now(), now()),
                                                                               (1, 2, now(), now()),
                                                                               (2, 2, now(), now()),
                                                                               (3, 2, now(), now());