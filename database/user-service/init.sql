-- =======================================================
-- 1. DATABASE: user_db (Dành cho User Service)
-- =======================================================

-- Xóa bảng cũ nếu tồn tại để tạo lại cho sạch (Cẩn thận khi chạy trên Prod)
DROP TABLE IF EXISTS addresses;
DROP TABLE IF EXISTS users;

CREATE TABLE IF NOT EXISTS users (
                                     id BIGSERIAL PRIMARY KEY,
                                     user_id VARCHAR(255) UNIQUE NOT NULL,    -- UUID cho JWT
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
                                         user_id VARCHAR(255) REFERENCES users(user_id) ON DELETE CASCADE,
                                         street VARCHAR(255) NOT NULL,
                                         city VARCHAR(100) NOT NULL,
                                         country VARCHAR(100) NOT NULL,
                                         zip_code VARCHAR(20)
);

CREATE INDEX IF NOT EXISTS idx_users_userid ON users(user_id);

-- Index
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);

-- =======================================================
-- 2. DATA SEEDING
-- =======================================================

-- Tạo Admin & User mẫu
INSERT INTO users (username, email, password_hash, full_name, role, phone)
VALUES
    ('admin', 'admin@example.com', '$2a$10$wW5.jR.k.1xQ.u.1xQ.u.e1xQ.u.1xQ.u.1xQ.u.1xQ.u.1xQ.u', 'System Admin', 'ADMIN', '0909000001'),
    ('user', 'user@example.com', '$2a$10$wW5.jR.k.1xQ.u.1xQ.u.e1xQ.u.1xQ.u.1xQ.u.1xQ.u.1xQ.u', 'Nguyen Van A', 'CUSTOMER', '0909000002')
ON CONFLICT (username) DO NOTHING;

-- Script sinh 1000 Users tự động
DO $$
    DECLARE
        i INT;
        new_user_id BIGINT;
    BEGIN
        FOR i IN 1..1000 LOOP
                INSERT INTO users (username, email, password_hash, full_name, phone, role)
                VALUES (
                           'customer_' || i,
                           'customer_' || i || '@test.com',
                           '$2a$10$wW5.jR.k.1xQ.u.1xQ.u.e1xQ.u.1xQ.u.1xQ.u.1xQ.u.1xQ.u', -- Pass giả định
                           'Khách Hàng ' || i,
                           '09' || LPAD((FLOOR(RANDOM() * 100000000))::text, 8, '0'),
                           'CUSTOMER'
                       )
                ON CONFLICT (username) DO NOTHING
                RETURNING user_id INTO new_user_id;

                -- Nếu insert thành công (không bị conflict), tạo địa chỉ
                IF new_user_id IS NOT NULL THEN
                    INSERT INTO addresses (user_id, street, city, country, zip_code)
                    VALUES (new_user_id, FLOOR(RANDOM() * 999 + 1) || ' Đường số ' || FLOOR(RANDOM() * 20 + 1), CASE WHEN i%2=0 THEN 'Hà Nội' ELSE 'TP.HCM' END, 'Vietnam', '700000');
                END IF;
        END LOOP;
    END $$;
