# Exercise 01: JWT

Extension of **Exercise 00** (Education Center API) with **JWT-based authentication** and
**role-based access control** using Spring Security.

## Added tech

- **Spring Security** (`spring-boot-starter-security`)
- **JJWT 0.12.6** (`jjwt-api`, `jjwt-impl`, `jjwt-jackson`) for token generation/parsing
- Spring Security Test (`spring-security-test`) for tests

## JWT behavior

- JWT token contains: **user ID**, **user role**, **user login** (plus issued-at/expiry).
- Tokens are signed with an **HS256 secret key** defined in `application.properties` (`jwt.secret`).
- Expiration is configurable via `jwt.expiration-ms`.
- Request authorization is stateless: the JWT is read from the `Authorization` header
  (`Bearer <token>`), and **no database access** is performed to authorize a request —
  all required info lives in the token.

## Authentication flow

1. Client sends `POST /signUp` with `{ "login": ..., "password": ... }`.
2. If credentials are **correct** → `200` + `{ "token": "<jwt>" }`.
3. If credentials are **wrong** → `403`.
4. Client then sends each API request with `Authorization: Bearer <jwt>`.

## Access rules

| HTTP method | Required role |
|-------------|---------------|
| GET | any authenticated role |
| POST | `ADMINISTRATOR` |
| PUT | `ADMINISTRATOR` |
| DELETE | `ADMINISTRATOR` |
| POST `/signUp` | public (no token) |

Rules are enforced centrally in `SecurityConfig` via URL matchers
(`hasRole("ADMINISTRATOR")`), with stateless sessions and CSRF disabled.

## Security components

| Component | Role |
|-----------|------|
| `JwtService` | generates and parses signed JWT tokens (secret from `application.properties`) |
| `JwtAuthenticationFilter` (`OncePerRequestFilter`) | reads `Authorization` header, validates token, builds authentication from claims, populates `SecurityContext` |
| `JwtAuthenticationToken` (`Authentication`) | custom `Authentication` (carries userId, login, ROLE_ authority) |
| `CustomAuthenticationProvider` (`AuthenticationProvider`) | validates login/password against `User` for `/signUp` |
| `SecurityConfig` | `SecurityFilterChain`, stateless policy, role-based URL rules, JWT filter registration |

## Swagger with JWT authorization

The `OpenApiConfig` registers a **bearer security scheme** (`bearer-jwt`), so Swagger UI shows
an **Authorize** button:

1. `POST /signUp` to get a JWT (e.g. `admin` / `admin123` from `data.sql`).
2. Click **Authorize** (top-right) and paste the token.
3. All subsequent requests from the UI include `Authorization: Bearer <token>`.

Endpoints (Users, Courses, Lessons, Students, Teachers) are the same as **Exercise 00** —
see `../ex00/README.md` for the full endpoint list.

## Files / structure

```
Education-Center/
├── src/main/java/fr/_42/educationcenter/
│   ├── config/           OpenAPI + SecurityConfig
│   ├── controllers/      AuthController (+ Users/Courses controllers)
│   ├── dto/              DTOs incl. SignUpRequest / SignUpResponse
│   ├── exceptions/       Custom exceptions + global handler
│   ├── models/           JPA entities (User, Course, Lesson, UserRole)
│   ├── repositories/     Spring Data JPA repositories
│   ├── security/         JWT + Spring Security components
│   └── services/         Business logic
└── src/main/resources/
    ├── application.properties   (incl. jwt.secret, jwt.expiration-ms)
    └── data.sql                5 users (1 admin, 2 teachers, 2 students)
```

## Running the app

1. PostgreSQL database `education_center` (config in `application.properties`).
2. `data.sql` seeds users (`ON CONFLICT (login) DO NOTHING`).
3. Run:

```bash
mvn spring-boot:run
```

4. Swagger UI: `http://localhost:8080/swagger-ui/index.html`

## Tests

- `UsersControllerTest` — verifies all user endpoints return **403** without a JWT token.
- H2 is used as the embedded test database; JWT secret/expiration are defined in the test config.

```bash
mvn test
```

## Notes

- Spring Data REST is **not** used.
- Passwords are stored in plain text (out of scope for this exercise).