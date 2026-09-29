# RideLink

Backend microservices for a fictional ride-sharing platform (IT3130 group assignment).
No frontend is required. Use **Swagger UI** or the shared **Postman collection**.

## Services and ownership

| Service | Port | Mongo database | Primary owner |
|---|---|---|---|
| Account Service | 8081 | `ridelink_account` | Member 1 |
| Driver & Vehicle Service | 8082 | `ridelink_driver` | Member 2 |
| Ride Management Service | 8083 | `ridelink_ride` | Member 3 |
| Fare & Payment Service | 8084 | `ridelink_fare` | Member 4 |

Each service owns its own MongoDB database. Services never query another service’s collections. They share identifiers (`accountId`, `rideId`) over REST and RabbitMQ.

## Prerequisites

- Java 21
- Maven Wrapper (included in each service)
- MongoDB listening on `localhost:27017` (no password required for local demo)
- RabbitMQ listening on `localhost:5672` with the default `guest`/`guest` account (used for ride completed/cancelled events)

## Configuration

Secrets are **not** committed. Override with environment variables (see [`.env.example`](.env.example)):

| Variable | Purpose | Local default |
|---|---|---|
| `MONGODB_URI` | Per-service Mongo URI | `mongodb://localhost:27017/ridelink_*` |
| `JWT_SECRET` | HMAC key for tokens (same value on all four services) | `ridelink-dev-jwt-secret-change-me-32bytes` |
| `RABBITMQ_HOST` / `PORT` / `USERNAME` / `PASSWORD` | Message broker | localhost / 5672 / guest / guest |
| `DRIVER_SERVICE_URL` | Ride → Driver REST | `http://localhost:8082` |
| `FARE_SERVICE_URL` | Ride → Fare REST | `http://localhost:8084` |

## Start-up order

1. MongoDB
2. RabbitMQ
3. Account Service (`8081`)
4. Driver & Vehicle Service (`8082`)
5. Fare & Payment Service (`8084`)
6. Ride Management Service (`8083`) last, because it calls Driver and Fare

```bash
cd account-service && ./mvnw spring-boot:run
cd driver-vehicle-service && ./mvnw spring-boot:run
cd fare-payment-service && ./mvnw spring-boot:run
cd ride-service && ./mvnw spring-boot:run
```

Swagger UI:

- http://localhost:8081/swagger-ui.html
- http://localhost:8082/swagger-ui.html
- http://localhost:8083/swagger-ui.html
- http://localhost:8084/swagger-ui.html

OpenAPI JSON: `http://localhost:808x/v3/api-docs`

## Sample credentials (seeded on Account Service startup)

| Email | Password | Role |
|---|---|---|
| `admin@ridelink.local` | `Admin123!` | ADMIN |
| `passenger@ridelink.local` | `Pass123!` | PASSENGER |
| `driver@ridelink.local` | `Drive123!` | DRIVER |

Login: `POST http://localhost:8081/api/auth/login` with `{ "email", "password" }`. Put the token in `Authorization: Bearer <token>` for the other services.

## Business rules

**Driver assignment:** Ride Service calls `GET /api/drivers/eligible?area={pickup}` and assigns the **first** AVAILABLE driver in that service area.

**Fare rule:** `fare = 150 + 50 * km + 10 * minutes`.

- If pickup and destination coordinates are supplied, `km` is Haversine distance.
- Otherwise a place-name table is used: Colombo–Kandy 115 km, Colombo–Galle 116 km, Colombo–Negombo 37 km, Kandy–Nuwara Eliya 77 km, **default 8 km**.
- `minutes = km * 1.5` (about 40 km/h).

**Ride lifecycle:** `REQUESTED → ASSIGNED → ACCEPTED → IN_PROGRESS → COMPLETED`.  
Cancel is allowed from `REQUESTED`, `ASSIGNED`, or `ACCEPTED` only. Invalid transitions return **409**.

**Simulated payment failure:** paying with `cardEnding` `0000` returns **402**.

## Interservice communication

| Interaction | Mechanism | Why |
|---|---|---|
| Ride → eligible drivers | Synchronous REST | Assignment needs an immediate answer |
| Ride → fare estimate | Synchronous REST | Passenger waits on a number |
| Ride completed / cancelled → Fare | RabbitMQ topic `ridelink.rides` (`ride.completed`, `ride.cancelled`) | Payment bookkeeping must not block ride completion |

## Tests

From each service directory:

```bash
./mvnw test
```

CI (GitHub Actions) runs `./mvnw -q test` for all four services on push and pull request to `main`.

## Postman

Import [`postman/RideLink.postman_collection.json`](postman/RideLink.postman_collection.json) and [`postman/RideLink.postman_environment.json`](postman/RideLink.postman_environment.json).

## Branching

Use feature branches (`feature/account-auth`, `feature/driver-ops`, `feature/ride-lifecycle`, `feature/fare-payment`) and pull requests into `main`. `main` is the demonstrable integrated version.
