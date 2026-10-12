-- One line of an order: a product, how many, and its price when ordered. Mirrors the OrderItem Java class.
-- unit_price is copied from products at order time, so later price changes don't affect old orders.
CREATE TABLE order_items (
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    order_id   BIGINT NOT NULL REFERENCES orders (id),
    product_id BIGINT NOT NULL REFERENCES products (id),
    quantity   INTEGER NOT NULL CHECK (quantity >= 1),
    unit_price NUMERIC(10, 2) NOT NULL CHECK (unit_price >= 0),
    UNIQUE (order_id, product_id)
);
