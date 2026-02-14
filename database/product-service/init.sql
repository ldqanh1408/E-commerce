-- =======================================================
-- PRODUCT SERVICE DATABASE INITIALIZATION
-- Database: product_db
-- =======================================================

-- --- PHẦN 1: AXON FRAMEWORK TABLES (Bắt buộc cho CQRS) ---

-- Bảng lưu Token (Vị trí đọc Event của Projection)
CREATE TABLE IF NOT EXISTS token_entry (
                                           processor_name VARCHAR(255) NOT NULL,
    segment INTEGER NOT NULL,
    token BYTEA,
    token_type VARCHAR(255),
    timestamp VARCHAR(255),
    owner VARCHAR(255),
    PRIMARY KEY (processor_name, segment)
    );

-- Bảng lưu Snapshot (Tăng tốc load Aggregate lớn)
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

CREATE TABLE IF NOT EXISTS categories (
                                          id BIGSERIAL PRIMARY KEY,
                                          name VARCHAR(100) NOT NULL,
    description TEXT,
    parent_id BIGINT REFERENCES categories(id)
    );

CREATE TABLE IF NOT EXISTS products (
                                        id BIGSERIAL PRIMARY KEY,
                                        product_id VARCHAR(255) UNIQUE NOT NULL, -- Axon Aggregate ID (UUID)
    category_id BIGINT REFERENCES categories(id),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(19, 2) NOT NULL,
    sku VARCHAR(100) UNIQUE,
    image_url VARCHAR(500),
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

CREATE TABLE IF NOT EXISTS inventory (
                                         id BIGSERIAL PRIMARY KEY,
                                         product_id VARCHAR(255) UNIQUE NOT NULL, -- Link với Axon Aggregate ID
    quantity INT NOT NULL DEFAULT 0,
    reserved INT NOT NULL DEFAULT 0, -- Hàng đang được giữ trong giỏ/đơn chờ thanh toán
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );