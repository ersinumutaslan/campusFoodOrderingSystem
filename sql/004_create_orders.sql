-- A student's order from one restaurant. Mirrors the Order Java class; status mirrors OrderStatus.
-- The total is not stored: it is calculated from order_items.
CREATE TABLE orders (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id       BIGINT NOT NULL REFERENCES users (id),
    restaurant_id BIGINT NOT NULL REFERENCES restaurants (id),
    status        VARCHAR(20) NOT NULL DEFAULT 'PLACED'
                  CHECK (status IN ('PLACED', 'PREPARING', 'READY', 'COMPLETED', 'CANCELLED')),
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);
