-- Menu items. Each product belongs to one restaurant. Mirrors the Product Java class.
CREATE TABLE products (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    restaurant_id BIGINT NOT NULL REFERENCES restaurants (id),
    name          VARCHAR(100) NOT NULL,
    price         NUMERIC(10, 2) NOT NULL CHECK (price >= 0),
    available     BOOLEAN NOT NULL DEFAULT TRUE,
    UNIQUE (restaurant_id, name)
);
