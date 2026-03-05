-- =======================================================
-- 1. DATABASE: user_db (Dành cho User Service)
-- =======================================================

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

CREATE TABLE IF NOT EXISTS addresses (
                                         id BIGSERIAL PRIMARY KEY,
                                         user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
                                         street VARCHAR(255) NOT NULL,
                                         city VARCHAR(100) NOT NULL,
                                         country VARCHAR(100) NOT NULL,
                                         zip_code VARCHAR(20)
);

-- Index cho User Service
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_fullname ON users(full_name);
CREATE INDEX IF NOT EXISTS idx_addresses_user_id ON addresses(user_id);

-- Data Seeding (Dữ liệu mẫu)
INSERT INTO users (username, email, password_hash, full_name, role)
VALUES
    ('admin', 'admin@example.com', '$2a$10$wW5.jR.k.1xQ.u.1xQ.u.e1xQ.u.1xQ.u.1xQ.u.1xQ.u.1xQ.u', 'System Admin', 'ADMIN'),
    ('user', 'user@example.com', '$2a$10$wW5.jR.k.1xQ.u.1xQ.u.e1xQ.u.1xQ.u.1xQ.u.1xQ.u.1xQ.u', 'Nguyen Van A', 'CUSTOMER')
ON CONFLICT DO NOTHING;
