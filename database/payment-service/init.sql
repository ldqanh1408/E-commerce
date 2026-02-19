-- =======================================================
-- PAYMENT SERVICE DATABASE INITIALIZATION
-- Database: payment_db
-- =======================================================

-- --- PHẦN 1: AXON FRAMEWORK TABLES ---

-- --- PHẦN 2: BUSINESS TABLES (Read Model) ---

CREATE TABLE IF NOT EXISTS payments (
                                        id BIGSERIAL PRIMARY KEY,
                                        payment_id VARCHAR(255) UNIQUE NOT NULL, -- Axon Aggregate ID
    order_id VARCHAR(255) UNIQUE NOT NULL,   -- Link tới Order
    amount DECIMAL(19, 2) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING', -- PENDING, COMPLETED, FAILED
    payment_method VARCHAR(50),
    transaction_id VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );