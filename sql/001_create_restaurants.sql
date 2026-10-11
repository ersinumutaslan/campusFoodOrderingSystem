-- Campus restaurants. Mirrors the Restaurant Java class.
CREATE TABLE restaurants (
    id   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);
