-- =======================================================
-- 2. DATABASE: product_db (Dành cho Product Service)
-- =======================================================

-- Xóa bảng cũ nếu tồn tại để tạo lại cho sạch
DROP TABLE IF EXISTS inventory CASCADE;
DROP TABLE IF EXISTS products CASCADE;
DROP TABLE IF EXISTS categories CASCADE;


-- =======================================================
-- BUSINESS TABLES
-- =======================================================

CREATE TABLE IF NOT EXISTS categories (
                                          id BIGSERIAL PRIMARY KEY,
                                          name VARCHAR(100) NOT NULL,
                                          description TEXT,
                                          parent_id BIGINT REFERENCES categories(id)
);

CREATE TABLE IF NOT EXISTS products (
                                        id BIGSERIAL PRIMARY KEY,
                                        product_id VARCHAR(255) UNIQUE NOT NULL, -- Axon Aggregate ID
                                        category_id BIGINT REFERENCES categories(id),
                                        name VARCHAR(255) NOT NULL,
                                        description TEXT,
                                        price DECIMAL(19, 2) NOT NULL,
                                        quantity INT NOT NULL DEFAULT 0,
                                        sku VARCHAR(100) UNIQUE,
                                        image_url VARCHAR(500),
                                        active BOOLEAN DEFAULT TRUE,
                                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS inventory (
                                         id BIGSERIAL PRIMARY KEY,
                                         product_id VARCHAR(255) UNIQUE NOT NULL,
                                         quantity INT NOT NULL DEFAULT 0,
                                         reserved INT NOT NULL DEFAULT 0,
                                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_products_product_id ON products(product_id);
CREATE INDEX IF NOT EXISTS idx_inventory_product_id ON inventory(product_id);


-- =======================================================
-- DATA SEEDING
-- =======================================================
DO $$
    DECLARE
        i INT;
        prod_uuid VARCHAR;
        cat_id BIGINT;
        rand_price DECIMAL;
        rand_qty INT;
        p_name VARCHAR;
    BEGIN
        -- Tạo 5 Danh mục chính
        INSERT INTO categories (id, name, description) VALUES
            (1, 'Điện Thoại', 'Smartphone đời mới nhất'),
            (2, 'Laptop', 'Máy tính xách tay cho văn phòng và gaming'),
            (3, 'Đồng Hồ Thông Minh', 'Smartwatch theo dõi sức khỏe'),
            (4, 'Phụ Kiện Điện Tử', 'Tai nghe, cáp sạc, sạc dự phòng'),
            (5, 'Máy Tính Bảng', 'Tablet chính hãng')
        ON CONFLICT DO NOTHING;
        PERFORM setval('categories_id_seq', 5, true);

        -- Vòng lặp sinh 100 Sản Phẩm mẫu
        FOR i IN 1..100 LOOP
                prod_uuid := 'prod_' || i; -- Dùng ID đơn giản để dễ test
                cat_id := (i % 5) + 1;
                rand_price := FLOOR(RANDOM() * 40000000 + 100000);
                rand_qty := FLOOR(RANDOM() * 500 + 10);

                IF cat_id = 1 THEN p_name := 'Smartphone Pro Max Series ' || i;
                ELSIF cat_id = 2 THEN p_name := 'Laptop Gaming Ultra ' || i;
                ELSIF cat_id = 3 THEN p_name := 'Smartwatch Fitness Gen ' || i;
                ELSIF cat_id = 4 THEN p_name := 'Tai nghe Bluetooth Pro ' || i;
                ELSE p_name := 'Tablet Super Display ' || i;
                END IF;

                INSERT INTO products (product_id, category_id, name, description, price, quantity, sku, image_url, active)
                VALUES (
                           prod_uuid, cat_id, p_name,
                           'Đây là mô tả chi tiết cho sản phẩm ' || p_name,
                           rand_price, rand_qty,
                           'SKU-' || i,
                           'https://picsum.photos/seed/' || i || '/400/400',
                           CASE WHEN i%10=0 THEN FALSE ELSE TRUE END
                       ) ON CONFLICT (product_id) DO NOTHING;

                INSERT INTO inventory (product_id, quantity, reserved)
                VALUES (prod_uuid, rand_qty, FLOOR(RANDOM() * 5))
                ON CONFLICT (product_id) DO NOTHING;
            END LOOP;
    END $$;
