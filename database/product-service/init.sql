-- =======================================================
-- 2. DATABASE: product_db (Dành cho Product Service)
-- =======================================================

CREATE TABLE IF NOT EXISTS categories (
                                          id BIGSERIAL PRIMARY KEY,
                                          name VARCHAR(100) NOT NULL,
                                          description TEXT,
                                          parent_id BIGINT REFERENCES categories(id)
);

CREATE TABLE IF NOT EXISTS products (
                                        id BIGSERIAL PRIMARY KEY,
                                        product_id VARCHAR(255) UNIQUE NOT NULL, -- UUID từ Axon Aggregate
                                        category_id BIGINT REFERENCES categories(id),
                                        name VARCHAR(255) NOT NULL,
                                        description TEXT,
                                        price DECIMAL(19, 2) NOT NULL,
                                        quantity INT NOT NULL DEFAULT 0,         -- Số lượng hiển thị (Available)
                                        sku VARCHAR(100) UNIQUE,
                                        image_url VARCHAR(500),
                                        active BOOLEAN DEFAULT TRUE,
                                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS inventory (
                                         id BIGSERIAL PRIMARY KEY,
                                         product_id VARCHAR(255) UNIQUE NOT NULL, -- Link với Aggregate ID
                                         quantity INT NOT NULL DEFAULT 0,         -- Tồn kho thực tế
                                         reserved INT NOT NULL DEFAULT 0,         -- Số lượng đang giữ (trong giỏ/đơn)
                                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Index cho Product Service
CREATE INDEX IF NOT EXISTS idx_products_name_search ON products USING GIN (to_tsvector('simple', name));
CREATE INDEX IF NOT EXISTS idx_products_price ON products(price);
CREATE INDEX IF NOT EXISTS idx_products_category ON products(category_id);
CREATE INDEX IF NOT EXISTS idx_products_created_batch ON products (created_at DESC, id DESC);
CREATE INDEX IF NOT EXISTS idx_products_active ON products(active);
CREATE INDEX IF NOT EXISTS idx_inventory_product_id ON inventory(product_id);
