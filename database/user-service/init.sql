-- =======================================================
-- 1. DATABASE: user_db (Dành cho User Service)
-- =======================================================

CREATE TABLE IF NOT EXISTS users (
                                     id BIGSERIAL PRIMARY KEY,
                                     user_id VARCHAR(255) UNIQUE NOT NULL,    -- THÊM MỚI: Dùng UUID này để bỏ vào JWT Token
                                     username VARCHAR(50) NOT NULL UNIQUE,
                                     email VARCHAR(100) NOT NULL UNIQUE,
                                     password_hash VARCHAR(255) NOT NULL,
                                     full_name VARCHAR(100) NOT NULL,
                                     phone VARCHAR(20),
                                     role VARCHAR(20) DEFAULT 'CUSTOMER',
                                     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                     updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS addresses (
                                         id BIGSERIAL PRIMARY KEY,
                                         user_id VARCHAR(255) REFERENCES users(user_id) ON DELETE CASCADE, -- Tham chiếu theo UUID
                                         street VARCHAR(255) NOT NULL,
                                         city VARCHAR(100) NOT NULL,
                                         country VARCHAR(100) NOT NULL,
                                         zip_code VARCHAR(20)
);

-- Index
CREATE INDEX IF NOT EXISTS idx_users_userid ON users(user_id);
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);

-- Data Seeding (Tạo UUID ngẫu nhiên cho seed data)
INSERT INTO users (user_id, username, email, password_hash, full_name, role)
VALUES
    (gen_random_uuid()::varchar, 'admin', 'admin@example.com', '$2a$10$wW5.jR.k.1xQ.u.1xQ.u.e1xQ.u.1xQ.u.1xQ.u.1xQ.u.1xQ.u', 'System Admin', 'ADMIN'),
    (gen_random_uuid()::varchar, 'user', 'user@example.com', '$2a$10$wW5.jR.k.1xQ.u.1xQ.u.e1xQ.u.1xQ.u.1xQ.u.1xQ.u.1xQ.u', 'Nguyen Van A', 'CUSTOMER')
ON CONFLICT DO NOTHING;


-- =======================================================
-- 1. CHẠY SCRIPT NÀY TẠI DB: user_db
-- Sinh ra 1000 Users và Địa chỉ (Kèm Admin)
-- =======================================================
DO $$
    DECLARE
        i INT;
        user_uuid VARCHAR;
    BEGIN
        -- Tạo 1 Admin chuẩn
        INSERT INTO users (user_id, username, email, password_hash, full_name, role)
        VALUES ('10000000-0000-0000-0000-000000000000', 'admin', 'admin@example.com', '$2a$10$wW5.jR.k.1xQ.u.1xQ.u.e1xQ.u.1xQ.u.1xQ.u.1xQ.u.1xQ.u', 'System Admin', 'ADMIN')
        ON CONFLICT DO NOTHING;

        -- Vòng lặp sinh 1000 Customer
        FOR i IN 1..1000 LOOP
                -- Định dạng UUID: 10000000-0000-0000-0000-000000000001 đến 1000
                user_uuid := '10000000-0000-0000-0000-' || LPAD(i::text, 12, '0');

                INSERT INTO users (user_id, username, email, password_hash, full_name, phone, role)
                VALUES (
                           user_uuid,
                           'customer_' || i,
                           'customer_' || i || '@test.com',
                           '$2a$10$wW5.jR.k.1xQ.u.1xQ.u.e1xQ.u.1xQ.u.1xQ.u.1xQ.u.1xQ.u', -- Pass: password123
                           'Khách Hàng ' || i,
                           '09' || LPAD((RANDOM() * 100000000)::INT::text, 8, '0'),
                           'CUSTOMER'
                       ) ON CONFLICT DO NOTHING;

                -- Sinh ngẫu nhiên 1-2 địa chỉ cho mỗi user
                INSERT INTO addresses (user_id, street, city, country, zip_code)
                VALUES (user_uuid, FLOOR(RANDOM() * 999 + 1) || ' Đường số ' || FLOOR(RANDOM() * 20 + 1), CASE WHEN i%2=0 THEN 'Hà Nội' ELSE 'TP.HCM' END, 'Vietnam', '700000');
            END LOOP;
    END $$;
