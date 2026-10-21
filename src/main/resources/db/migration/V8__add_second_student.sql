-- A second sample student with one order, so "students only see their own orders" can be tried out.
-- Password: "campus123" (local development only), same hash as in V7.
INSERT INTO users (name, email, role, password_hash) VALUES
    ('Ali Kaya', 'ali@campus.edu', 'STUDENT', '{bcrypt}$2a$10$bnRkydUdF6L0F2PEQltd6u86AGPj8zmio5mHa1tbWKCWse1WY3S.S');

INSERT INTO orders (user_id, restaurant_id, status)
VALUES ((SELECT id FROM users WHERE email = 'ali@campus.edu'),
        (SELECT id FROM restaurants WHERE name = 'Pizza Corner'),
        'PLACED');

INSERT INTO order_items (order_id, product_id, quantity, unit_price)
VALUES ((SELECT max(id) FROM orders),
        (SELECT id FROM products WHERE name = 'Margherita'),
        1, 150.00);
