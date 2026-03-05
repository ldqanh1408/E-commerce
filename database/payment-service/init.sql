-- =======================================================
-- 4. DATABASE: payment_db (Dành cho Payment Service)
-- =======================================================

CREATE TABLE IF NOT EXISTS payments (
                                        id BIGSERIAL PRIMARY KEY,
                                        payment_id VARCHAR(255) UNIQUE NOT NULL, -- UUID
                                        order_id VARCHAR(255) UNIQUE NOT NULL,
                                        amount DECIMAL(19, 2) NOT NULL,
                                        status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
                                        payment_method VARCHAR(50),
                                        transaction_id VARCHAR(255),
                                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Index cho Payment Service
CREATE INDEX IF NOT EXISTS idx_payments_order_id ON payments(order_id);