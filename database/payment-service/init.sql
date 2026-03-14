-- =======================================================
-- 4. DATABASE: payment_db (Dành cho Payment Service)
-- =======================================================

CREATE TABLE IF NOT EXISTS payments (
                                        id BIGSERIAL PRIMARY KEY,
                                        payment_id VARCHAR(255) UNIQUE NOT NULL,     -- Axon Aggregate ID
                                        order_id VARCHAR(255) UNIQUE NOT NULL,
                                        amount DECIMAL(19, 2) NOT NULL,
                                        status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
                                        payment_method VARCHAR(50),
                                        transaction_id VARCHAR(255),
                                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_payments_payment_id ON payments(payment_id);
CREATE INDEX IF NOT EXISTS idx_payments_order_id ON payments(order_id);


-- =======================================================
-- 4. CHẠY SCRIPT NÀY TẠI DB: payment_db
-- Sinh ra 1000 Thanh toán, link chính xác tới 1000 Orders
-- =======================================================
DO $$
    DECLARE
        i INT;
        pay_uuid VARCHAR;
        o_uuid VARCHAR;
        pay_status VARCHAR;
    BEGIN
        FOR i IN 1..1000 LOOP
                pay_uuid := '40000000-0000-0000-0000-' || LPAD(i::text, 12, '0');
                o_uuid := '30000000-0000-0000-0000-' || LPAD(i::text, 12, '0');

                -- Tình trạng thanh toán
                IF i%5 = 0 THEN pay_status := 'FAILED';
                ELSIF i%2 = 0 THEN pay_status := 'COMPLETED';
                ELSE pay_status := 'PENDING';
                END IF;

                INSERT INTO payments (payment_id, order_id, amount, status, payment_method, transaction_id, created_at)
                VALUES (
                           pay_uuid,
                           o_uuid,
                           (RANDOM() * 20000000 + 500000)::DECIMAL(19,2),
                           pay_status,
                           CASE WHEN i%2=0 THEN 'STRIPE' ELSE 'VNPAY' END,
                           'TXN_' || EXTRACT(EPOCH FROM NOW())::INT || '_' || i,
                           NOW() - (RANDOM() * 365 || ' days')::INTERVAL
                       ) ON CONFLICT DO NOTHING;
            END LOOP;
    END $$;