-- =======================================================
-- 3. DATABASE: order_db (Dành cho Order Service)
-- =======================================================

CREATE TABLE IF NOT EXISTS orders (
                                      id BIGSERIAL PRIMARY KEY,
                                      order_id VARCHAR(255) UNIQUE NOT NULL,   -- UUID
                                      user_id BIGINT NOT NULL,
                                      total_amount DECIMAL(19, 2) NOT NULL,
                                      status VARCHAR(50) NOT NULL DEFAULT 'CREATED',
                                      shipping_address TEXT NOT NULL,
                                      reason VARCHAR(255),                     -- Lý do hủy (nếu có)
                                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS order_items (
                                           id BIGSERIAL PRIMARY KEY,
                                           order_db_id BIGINT REFERENCES orders(id) ON DELETE CASCADE,
                                           product_id VARCHAR(255) NOT NULL,
                                           product_name VARCHAR(255) NOT NULL,      -- Snapshot tên SP
                                           price DECIMAL(19, 2) NOT NULL,           -- Snapshot giá SP
                                           quantity INT NOT NULL,
                                           sub_total DECIMAL(19, 2) NOT NULL
);

-- Index cho Order Service
CREATE INDEX IF NOT EXISTS idx_orders_user_id ON orders(user_id);
CREATE INDEX IF NOT EXISTS idx_orders_status ON orders(status);