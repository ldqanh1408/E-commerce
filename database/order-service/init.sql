-- =======================================================
-- ORDER SERVICE DATABASE INITIALIZATION
-- Database: order_db
-- =======================================================

-- --- PHẦN 1: AXON FRAMEWORK TABLES (Quan trọng nhất: Saga) ---

CREATE TABLE IF NOT EXISTS token_entry (
    processor_name VARCHAR(255) NOT NULL,
    segment INTEGER NOT NULL,
    token BYTEA,
    token_type VARCHAR(255),
    timestamp VARCHAR(255),
    owner VARCHAR(255),
    PRIMARY KEY (processor_name, segment)
    );

-- Bảng lưu Saga (Quản lý giao dịch phân tán)
CREATE TABLE IF NOT EXISTS saga_entry (
    saga_id VARCHAR(255) NOT NULL,
    revision VARCHAR(255),
    saga_type VARCHAR(255),
    serialized_saga BYTEA,
    PRIMARY KEY (saga_id)
);

-- Bảng liên kết Saga (Association Values)
CREATE TABLE IF NOT EXISTS association_value_entry (
    id BIGSERIAL PRIMARY KEY,
    saga_id VARCHAR(255) NOT NULL,
    association_key VARCHAR(255),
    association_value VARCHAR(255),
    saga_type VARCHAR(255)
);
CREATE INDEX IF NOT EXISTS idx_association_saga_id ON association_value_entry (saga_id);
CREATE INDEX IF NOT EXISTS idx_association_key_value ON association_value_entry (association_key, association_value);

CREATE TABLE IF NOT EXISTS snapshot_event_entry (
    aggregate_identifier VARCHAR(255) NOT NULL,
    sequence_number BIGINT NOT NULL,
    type VARCHAR(255) NOT NULL,
    event_identifier VARCHAR(255) NOT NULL,
    meta_data BYTEA,
    payload BYTEA NOT NULL,
    payload_revision VARCHAR(255),
    payload_type VARCHAR(255) NOT NULL,
    timestamp VARCHAR(255) NOT NULL,
    PRIMARY KEY (aggregate_identifier, sequence_number, type)
);

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