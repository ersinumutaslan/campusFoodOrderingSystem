-- Sample data for local development.
-- On a fresh database the ids are generated 1, 2, 3... in insert order.

INSERT INTO restaurants (name) VALUES
    ('Campus Grill'),   -- id 1
    ('Pizza Corner'),   -- id 2
    ('Burger House');   -- id 3

INSERT INTO products (restaurant_id, name, price, available) VALUES
    (1, 'Chicken Wrap',  85.50, TRUE),   -- id 1
    (1, 'Ayran',         20.00, TRUE),   -- id 2
    (1, 'Lentil Soup',   40.00, FALSE),  -- id 3 (sold out)
    (2, 'Margherita',   150.00, TRUE),   -- id 4
    (3, 'Cheeseburger', 120.00, TRUE);   -- id 5

INSERT INTO users (name, email, role) VALUES
    ('Ayşe Yılmaz',  'ayse@campus.edu',   'STUDENT'),  -- id 1
    ('Mehmet Demir', 'mehmet@campus.edu', 'STAFF'),    -- id 2
    ('Zeynep Kaya',  'zeynep@campus.edu', 'ADMIN');    -- id 3

INSERT INTO orders (user_id, restaurant_id, status) VALUES
    (1, 1, 'READY'),    -- id 1: Ayşe at Campus Grill
    (1, 2, 'PLACED'),   -- id 2: Ayşe at Pizza Corner
    (1, 3, 'PLACED');   -- id 3: Ayşe at Burger House

INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES
    (1, 1, 2,  85.50),  -- 2 x Chicken Wrap
    (1, 2, 1,  20.00),  -- 1 x Ayran
    (2, 4, 1, 150.00),  -- 1 x Margherita
    (3, 5, 2, 120.00);  -- 2 x Cheeseburger
