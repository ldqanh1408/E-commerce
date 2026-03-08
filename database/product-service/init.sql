-- =======================================================
-- 2. DATABASE: product_db (Dành cho Product Service)
-- =======================================================
-- (Phần này của bạn đã quá chuẩn, mình giữ nguyên 100%)

CREATE TABLE IF NOT EXISTS categories (
                                          id BIGSERIAL PRIMARY KEY,
                                          name VARCHAR(100) NOT NULL,
                                          description TEXT,
                                          parent_id BIGINT REFERENCES categories(id)
);

CREATE TABLE IF NOT EXISTS products (
                                        id BIGSERIAL PRIMARY KEY,
                                        product_id VARCHAR(255) UNIQUE NOT NULL,
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

CREATE INDEX IF NOT EXISTS idx_products_name_search ON products USING GIN (to_tsvector('simple', name));
CREATE INDEX IF NOT EXISTS idx_inventory_product_id ON inventory(product_id);



-- =======================================================
-- 2. CHẠY SCRIPT NÀY TẠI DB: product_db
-- Sinh ra 5 Danh mục, 1000 Sản phẩm đa dạng, 1000 Tồn kho
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
        -- Reset lại sequence của categories
        PERFORM setval('categories_id_seq', 5, true);

        -- Vòng lặp sinh 1000 Sản Phẩm
        FOR i IN 1..1000 LOOP
                -- Định dạng UUID: 20000000-0000-0000-0000-000000000001 đến 1000
                prod_uuid := '20000000-0000-0000-0000-' || LPAD(i::text, 12, '0');
                cat_id := (i % 5) + 1;
                rand_price := FLOOR(RANDOM() * 40000000 + 100000); -- Giá từ 100k -> 40tr
                rand_qty := FLOOR(RANDOM() * 500 + 10); -- Số lượng 10 -> 500

                -- Tạo tên đa dạng theo danh mục
                IF cat_id = 1 THEN p_name := 'Smartphone Pro Max Series ' || i;
                ELSIF cat_id = 2 THEN p_name := 'Laptop Gaming Ultra ' || i;
                ELSIF cat_id = 3 THEN p_name := 'Smartwatch Fitness Gen ' || i;
                ELSIF cat_id = 4 THEN p_name := 'Tai nghe Bluetooth Pro ' || i;
                ELSE p_name := 'Tablet Super Display ' || i;
                END IF;

                INSERT INTO products (product_id, category_id, name, description, price, quantity, sku, image_url, active)
                VALUES (
                           prod_uuid, cat_id, p_name,
                           'Đây là mô tả chi tiết vô cùng hấp dẫn cho sản phẩm ' || p_name,
                           rand_price, rand_qty,
                           'SKU-' || i || '-' || FLOOR(RANDOM()*9999),
                           'https://picsum.photos/seed/' || i || '/400/400', -- Ảnh ngẫu nhiên
                           CASE WHEN i%10=0 THEN FALSE ELSE TRUE END -- 10% sản phẩm bị vô hiệu hóa
                       ) ON CONFLICT DO NOTHING;

                INSERT INTO inventory (product_id, quantity, reserved)
                VALUES (prod_uuid, rand_qty, FLOOR(RANDOM() * 5))
                ON CONFLICT DO NOTHING;
            END LOOP;
    END $$;
