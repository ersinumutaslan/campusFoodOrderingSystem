# CampusFoodOrdering

Backend for a university cafeteria food-ordering system. Students browse restaurants and menus,
place orders and track their status. Staff manage products and process orders. Admins manage users
and restaurants.

Portfolio project for learning backend engineering with Java and Spring Boot.

## Requirements

- Java 25
- Maven 3.9+
- Docker (for the PostgreSQL database)

## Run

```bash
docker compose up -d       # start PostgreSQL on port 5433
mvn spring-boot:run        # start the API on http://localhost:8080
```

Flyway creates the tables and sample data on first start.

## Endpoints

| Method | Path | Description |
|---|---|---|
| GET | `/restaurants` | List all restaurants |
| GET | `/restaurants/{id}/products` | Available menu of one restaurant (404 if unknown) |
| POST | `/orders` | Place an order (201; 400 for invalid data or broken rules, 404 for unknown ids) |
| GET | `/orders/{id}` | One order with its items and total (404 if unknown) |

## Run tests

```bash
mvn test
```
