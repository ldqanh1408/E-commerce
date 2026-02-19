-- =======================================================
-- PRODUCT SERVICE DATABASE INITIALIZATION
-- Database: product_db
-- =======================================================

-- --- PHẦN 1: AXON FRAMEWORK TABLES (Bắt buộc cho CQRS) ---


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

-- Creating Indexes for Products
CREATE INDEX IF NOT EXISTS idx_products_price ON products(price);
CREATE INDEX IF NOT EXISTS idx_products_name ON products(name);
CREATE INDEX IF NOT EXISTS idx_products_created_at ON products(created_at);
CREATE INDEX IF NOT EXISTS idx_products_price_pid ON products(price, product_id);
-- --- 1. Fake Data: Categories ---
INSERT INTO categories (name, description, parent_id) VALUES
                                                          ('Electronics', 'Các thiết bị điện tử, công nghệ', NULL),
                                                          ('Smartphones', 'Điện thoại thông minh các loại', 1),
                                                          ('Laptops', 'Máy tính xách tay phục vụ làm việc và chơi game', 1),
                                                          ('Fashion', 'Thời trang nam nữ', NULL);

-- --- 2. Fake Data: Products ---
-- Lưu ý: product_id ở đây là chuỗi (mô phỏng UUID của Axon) để dễ test

-- Sản phẩm 1: iPhone 15 Pro
INSERT INTO products (product_id, category_id, name, description, price, sku, image_url, active) VALUES
    ('prod-iphone-15', 2, 'iPhone 15 Pro Max 256GB', 'Titanium design, A17 Pro chip, 48MP camera system.', 34990000.00, 'APPLE-IP15-PM-256', 'https://example.com/iphone15promax.jpg', TRUE);

-- Sản phẩm 2: Samsung S24 Ultra
INSERT INTO products (product_id, category_id, name, description, price, sku, image_url, active) VALUES
    ('prod-s24-ultra', 2, 'Samsung Galaxy S24 Ultra 512GB', 'Galaxy AI, Titanium Frame, 200MP Camera.', 31500000.00, 'SAM-S24U-512', 'https://example.com/s24ultra.jpg', TRUE);

-- Sản phẩm 3: Macbook Air M2
INSERT INTO products (product_id, category_id, name, description, price, sku, image_url, active) VALUES
    ('prod-mac-m2', 3, 'MacBook Air M2 13 inch 8GB/256GB', 'Siêu mỏng nhẹ, chip M2 mạnh mẽ, pin trâu.', 24890000.00, 'APPLE-MAC-M2-13', 'https://example.com/macbookairm2.jpg', TRUE);

-- Sản phẩm 4: Dell XPS 13
INSERT INTO products (product_id, category_id, name, description, price, sku, image_url, active) VALUES
    ('prod-dell-xps', 3, 'Dell XPS 13 Plus 9320', 'Thiết kế tương lai, màn hình OLED 3.5K touch.', 45000000.00, 'DELL-XPS-13-PLUS', 'https://example.com/dellxps13.jpg', TRUE);

-- Sản phẩm 5: Xiaomi 14 (Giá rẻ hơn để test sort)
INSERT INTO products (product_id, category_id, name, description, price, sku, image_url, active) VALUES
    ('prod-xiaomi-14', 2, 'Xiaomi 14 12GB/256GB', 'Leica Camera, Snapdragon 8 Gen 3.', 19990000.00, 'MI-14-256', 'https://example.com/xiaomi14.jpg', TRUE);

-- Sản phẩm 6: Sony Headphones (Test active = false)
INSERT INTO products (product_id, category_id, name, description, price, sku, image_url, active) VALUES
    ('prod-sony-xm5', 1, 'Sony WH-1000XM5 Noise Canceling', 'Tai nghe chống ồn tốt nhất thế giới.', 8490000.00, 'SONY-XM5-BLK', 'https://example.com/sonyxm5.jpg', FALSE);

-- --- 3. Fake Data: Inventory ---
-- Số lượng tồn kho tương ứng với product_id bên trên

INSERT INTO inventory (product_id, quantity, reserved) VALUES
                                                           ('prod-iphone-15', 50, 2),   -- 50 cái kho, 2 cái đang trong giỏ hàng
                                                           ('prod-s24-ultra', 35, 0),   -- 35 cái kho
                                                           ('prod-mac-m2', 10, 5),      -- Hàng hot, giữ nhiều
                                                           ('prod-dell-xps', 5, 0),     -- Hàng hiếm
                                                           ('prod-xiaomi-14', 100, 0),  -- Tồn kho nhiều
                                                           ('prod-sony-xm5', 0, 0);     -- Hết hàng
