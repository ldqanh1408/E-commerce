-- =======================================================
-- 3. DATABASE: order_db (Dành cho Order Service)
-- =======================================================

CREATE TABLE IF NOT EXISTS orders (
                                      internal_id BIGSERIAL PRIMARY KEY,           -- Đổi tên từ 'id' cho đỡ nhầm lẫn
                                      order_id VARCHAR(255) UNIQUE NOT NULL,       -- UUID (Axon Aggregate ID)
                                      user_id VARCHAR(255) NOT NULL,               -- ĐỔI SANG VARCHAR ĐỂ LƯU UUID TỪ JWT
                                      total_amount DECIMAL(19, 2) NOT NULL,
                                      status VARCHAR(50) NOT NULL DEFAULT 'CREATED',
                                      payment_method VARCHAR(50) NOT NULL,         -- THÊM MỚI: Khách chọn Momo/Stripe/COD
                                      shipping_address TEXT NOT NULL,
                                      reason VARCHAR(255),
                                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS order_items (
                                           id BIGSERIAL PRIMARY KEY,
                                           order_internal_id BIGINT REFERENCES orders(internal_id) ON DELETE CASCADE, -- Đổi tên tham chiếu
                                           product_id VARCHAR(255) NOT NULL,            -- UUID của Product
                                           product_name VARCHAR(255) NOT NULL,
                                           price DECIMAL(19, 2) NOT NULL,
                                           quantity INT NOT NULL,
                                           sub_total DECIMAL(19, 2) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_orders_user_id ON orders(user_id);
CREATE INDEX IF NOT EXISTS idx_orders_order_id ON orders(order_id);

-- =======================================================
-- 3. CHẠY SCRIPT NÀY TẠI DB: order_db
-- Sinh ra 1000 Đơn hàng, link chính xác tới 1000 Users và Products
-- =======================================================
DO $$
    DECLARE
        i INT;
        o_uuid VARCHAR;
        u_uuid VARCHAR;
        p_uuid VARCHAR;
        internal_o_id BIGINT;
        rand_status VARCHAR;
    BEGIN
        FOR i IN 1..1000 LOOP
                -- Sinh UUID định dạng Order (3000...), User (1000...), Product (2000...)
                o_uuid := '30000000-0000-0000-0000-' || LPAD(i::text, 12, '0');
                u_uuid := '10000000-0000-0000-0000-' || LPAD(i::text, 12, '0'); -- Mỗi user mua 1 đơn
                p_uuid := '20000000-0000-0000-0000-' || LPAD(i::text, 12, '0'); -- Mỗi đơn mua 1 loại SP

                -- Ngẫu nhiên trạng thái đơn
                IF i%4 = 0 THEN rand_status := 'COMPLETED';
                ELSIF i%4 = 1 THEN rand_status := 'CONFIRMED';
                ELSIF i%4 = 2 THEN rand_status := 'CANCELLED';
                ELSE rand_status := 'PENDING';
                END IF;

                -- Thêm Order và lấy ID nội bộ (internal_id)
                INSERT INTO orders (order_id, user_id, total_amount, status, payment_method, shipping_address, created_at)
                VALUES (
                           o_uuid, u_uuid,
                           (RANDOM() * 20000000 + 500000)::DECIMAL(19,2),
                           rand_status,
                           CASE WHEN i%3=0 THEN 'COD' WHEN i%3=1 THEN 'STRIPE' ELSE 'VNPAY' END,
                           'Địa chỉ nhận hàng số ' || i || ', Trái Đất',
                           NOW() - (RANDOM() * 365 || ' days')::INTERVAL -- Lịch sử đặt hàng rải rác trong 1 năm qua
                       ) RETURNING internal_id INTO internal_o_id;

                -- Thêm Items cho Order đó
                INSERT INTO order_items (order_internal_id, product_id, product_name, price, quantity, sub_total)
                VALUES (internal_o_id, p_uuid, 'Sản phẩm mua ngẫu nhiên ' || i, 15000000, 1, 15000000);

                -- Thêm 1 item phụ (Giả sử mua 2 món)
                INSERT INTO order_items (order_internal_id, product_id, product_name, price, quantity, sub_total)
                VALUES (internal_o_id, '20000000-0000-0000-0000-000000000001', 'Smartphone Pro Max Series 1', 250000, 2, 500000);
            END LOOP;
    END $$;