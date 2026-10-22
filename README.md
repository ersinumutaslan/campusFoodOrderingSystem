# CampusFoodOrdering

Backend for a university cafeteria food-ordering system. Students browse restaurants and menus,
place orders and track their status. Staff manage products and process orders. Admins manage users
and restaurants.

Portfolio project for learning backend engineering with Java and Spring Boot.

**Live demo:** https://campusfoodorderingsystem.onrender.com (opens the interactive API docs).
It runs on a free server that sleeps when unused, so the first visit can take about a minute.
Log in with one of the demo users listed under [Login](#login).

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

### Run everything in Docker (no Java or Maven needed)

```bash
docker compose --profile app up -d --build   # build the app image, start database + app
docker compose --profile app down            # stop both (data is kept)
```

## Login

Requests log in with email + password (HTTP Basic). In Swagger UI, use the **Authorize** button.

Sample users (local development only, created by the Flyway migrations), all with the password `campus123`:

| Email | Role |
|---|---|
| ayse@campus.edu | STUDENT |
| ali@campus.edu | STUDENT |
| mehmet@campus.edu | STAFF |
| zeynep@campus.edu | ADMIN |

Browsing restaurants and menus needs no login. Placing an order needs STUDENT; changing an order's status needs STAFF.

## Endpoints

Interactive API docs (Swagger UI): http://localhost:8080/swagger-ui.html

| Method | Path | Description |
|---|---|---|
| GET | `/restaurants` | List all restaurants |
| GET | `/restaurants/{id}/products` | Available menu of one restaurant (404 if unknown) |
| POST | `/orders` | Place an order for the logged-in student (201; 400 for invalid data or broken rules, 404 for unknown ids) |
| GET | `/orders/mine` | The logged-in user's orders, newest first |
| GET | `/orders/{id}` | One order with its items and total; students only their own (403), staff and admins any |
| PATCH | `/orders/{id}/status` | Move an order to a new status, e.g. `{"status": "PREPARING"}` (409 if the change is not allowed) |

## Run tests

```bash
mvn test
```

Docker must be running: the integration tests use Testcontainers to start a throwaway PostgreSQL.
