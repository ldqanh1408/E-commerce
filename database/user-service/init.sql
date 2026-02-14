-- =======================================================
-- USER SERVICE DATABASE INITIALIZATION
-- Database: user_db
-- =======================================================

-- 1. Bảng Users
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    role VARCHAR(20) DEFAULT 'CUSTOMER', -- ADMIN, CUSTOMER
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Bảng Addresses (Sổ địa chỉ)
CREATE TABLE IF NOT EXISTS addresses (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    street VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    country VARCHAR(100) NOT NULL,
    zip_code VARCHAR(20)
);

-- (Optional) Insert Admin mặc định (Pass: password123 - Đã hash BCrypt)
INSERT INTO users (username, email, password_hash, full_name, role)
VALUES ('admin', 'admin@example.com', '$2a$10$wW5.jR.k.1xQ.u.1xQ.u.e1xQ.u.1xQ.u.1xQ.u.1xQ.u.1xQ.u', 'System Admin', 'ADMIN')
    ON CONFLICT DO NOTHING;