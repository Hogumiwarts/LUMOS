-- SampleData : 여러 기기를 보유한 경우, 하나만 보유한 경우, 보유한 디바이스가 없는 경우에 대한 테스트 가능

CREATE TABLE IF NOT EXISTS device (
    device_id BIGSERIAL PRIMARY KEY,
    installed_app_id VARCHAR(255),
    member_id BIGINT,
    device_url VARCHAR(255),
    control_id VARCHAR(255),
    tag_number INT,
    device_name VARCHAR(100),
    device_manufacturer VARCHAR(255),
    device_model VARCHAR(255),
    device_type VARCHAR(255),
    control JSON,
    created_at TIMESTAMP DEFAULT now(),
    updated_at TIMESTAMP
);

-- member_id = 1 (3개)
-- INSERT INTO device (device_id, installed_app_id, member_id, device_url, control_id, tag_number, device_name, control, created_at, updated_at)
-- VALUES (2, '11237955-08ec-418f-a2cd-8c7ca1a0919e', 1, 'http://localhost/device/2', '90884518-cfe8-4d94-a476-338682820822', 1001, '스위치(미니빅)', '{"power": "off"}', now(), now());
-- INSERT INTO device (device_id, installed_app_id, member_id, device_url, control_id, tag_number, device_name, control, created_at, updated_at)
-- VALUES (3, '11237955-08ec-418f-a2cd-8c7ca1a0919e',1, 'http://localhost/device/3', '3f3f9396-8d28-43ab-87da-255a02373591', 1002, '스피커(쉼포니스크)', '{"power": "on", "brightness": 50}', now(), now());
-- INSERT INTO device (device_id, installed_app_id, member_id, device_url, control_id, tag_number, device_name, control, created_at, updated_at)
-- VALUES (4, '11237955-08ec-418f-a2cd-8c7ca1a0919e',1, 'http://localhost/device/4', '12', 1003, '공기청정기', '{"activated": true}', now(), now());
--
-- -- member_id = 2 (5개)
-- INSERT INTO device (device_id, installed_app_id, member_id, device_url, control_id, tag_number, device_name, control, created_at, updated_at)
-- VALUES (5, '11237955-08ec-418f-a2cd-8c7ca1a0919e',2, 'http://localhost/device/5', '20', 2001, '에어컨', '{"temperature": 24, "power": "on"}', now(), now());
-- INSERT INTO device (device_id, installed_app_id, member_id, device_url, control_id, tag_number, device_name, control, created_at, updated_at)
-- VALUES (6, '11237955-08ec-418f-a2cd-8c7ca1a0919e',2, 'http://localhost/device/6', '21', 2002, '보일러', '{"temperature": 30}', now(), now());
-- INSERT INTO device (device_id, installed_app_id, member_id, device_url, control_id, tag_number, device_name, control, created_at, updated_at)
-- VALUES (7, '11237955-08ec-418f-a2cd-8c7ca1a0919e',2, 'http://localhost/device/7', '22', 2003, '세탁기', '{"status": "idle"}', now(), now());
-- INSERT INTO device (device_id, installed_app_id, member_id, device_url, control_id, tag_number, device_name, control, created_at, updated_at)
-- VALUES (8, '11237955-08ec-418f-a2cd-8c7ca1a0919e',2, 'http://localhost/device/8', '23', 2004, '스마트TV', '{"channel": 7}', now(), now());
-- INSERT INTO device (device_id, installed_app_id, member_id, device_url, control_id, tag_number, device_name, control, created_at, updated_at)
-- VALUES (9, '11237955-08ec-418f-a2cd-8c7ca1a0919e',2, 'http://localhost/device/9', '24', 2005, '커피머신', '{"power": "on"}', now(), now());
--
-- -- member_id = 3 (1개)
-- INSERT INTO device (device_id, installed_app_id, member_id, device_url, control_id, tag_number, device_name, control, created_at, updated_at)
-- VALUES (10, '11237955-08ec-418f-a2cd-8c7ca1a0919e',3, 'http://localhost/device/10', '30', 3001, '스마트 락', '{"locked": true}', now(), now());

-- -- member_id = 3 (1개)
-- INSERT INTO device (device_id, installed_app_id, member_id, device_url, control_id, tag_number, device_name, control, created_at, updated_at)
-- VALUES (12, '5f810cf2-432c-4c4c-bc72-c5af5abf1ef5',3, 'http://localhost/device/10', '9d7627a8-5240-4cd5-9183-09158a53f040', 3001, '알감자 조명', '{"activated" : true}', now(), now());