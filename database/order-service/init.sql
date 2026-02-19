-- =======================================================
-- ORDER SERVICE DATABASE INITIALIZATION
-- Database: order_db
-- =======================================================

-- --- PHẦN 1: AXON FRAMEWORK TABLES (Quan trọng nhất: Saga) ---

-- --- PHẦN 2: BUSINESS TABLES (Read Model) ---

CREATE TABLE IF NOT EXISTS orders (
    id BIGSERIAL PRIMARY KEY,
    order_id VARCHAR(255) UNIQUE NOT NULL, -- Axon Aggregate ID (UUID)
    user_id BIGINT NOT NULL,
    total_amount DECIMAL(19, 2) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'CREATED', -- CREATED, APPROVED, SHIPPED, CANCELLED
    shipping_address TEXT NOT NULL,
    reason VARCHAR(255), -- Lý do hủy (nếu có)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS order_items (
    id BIGSERIAL PRIMARY KEY,
    order_db_id BIGINT REFERENCES orders(id) ON DELETE CASCADE,
    product_id VARCHAR(255) NOT NULL, -- Axon Aggregate ID của Product
    product_name VARCHAR(255) NOT NULL,
    price DECIMAL(19, 2) NOT NULL,
    quantity INT NOT NULL,
    sub_total DECIMAL(19, 2) NOT NULL
);