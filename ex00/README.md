# Exercise 00: REST API

A full REST API for managing a training center, built with **Spring Boot**, following the
Richardson Maturity Model **Level 2** (proper resource URIs + HTTP verbs + CRUD per resource).

## Tech stack

- **Java 21 (LTS)** + Spring Boot 4.1.1
- Spring Data JPA + Hibernate
- PostgreSQL (development), H2 (tests)
- springdoc-openapi / Swagger UI for API documentation
- Lombok
- Validation (Jakarta Bean Validation)
- MockMvc for controller tests

## Project structure

```
Education-Center/
├── src/main/java/fr/_42/educationcenter/
│   ├── config/           OpenAPI (Swagger) bean configuration
│   ├── controllers/      REST controllers (Users, Courses + nested resources)
│   ├── dto/              Request/Response DTOs + error body
│   ├── exceptions/       Custom exceptions + global handler
│   ├── models/           JPA entities (User, Course, Lesson, UserRole)
│   ├── repositories/     Spring Data JPA repositories
│   └── services/         Business logic
└── src/main/resources/
    ├── application.properties
    └── data.sql          Seed data
```

## Entities

| Entity | Fields |
|--------|--------|
| **User** | id, firstName, lastName, role (`ADMINISTRATOR`/`TEACHER`/`STUDENT`), login (unique), password |
| **Course** | id, startDate, endDate, name, description, teachers (M-N), students (M-N), lessons (1-N) |
| **Lesson** | id, startTime, finishTime, dayOfWeek, teacher (M-1), course (M-1) |

## API endpoints

### Users
| Method | Path | Description |
|--------|------|-------------|
| GET | `/users` | Get all users (paginated) |
| GET | `/users/{userId}` | Get a user by id |
| POST | `/users` | Create a user (201) |
| PUT | `/users/{userId}` | Update a user |
| DELETE | `/users/{userId}` | Delete a user (204) |

### Courses
| Method | Path | Description |
|--------|------|-------------|
| GET | `/courses` | Get all courses (paginated) |
| GET | `/courses/{courseId}` | Get a course by id |
| POST | `/courses` | Create a course (201) |
| PUT | `/courses/{courseId}` | Update a course |
| DELETE | `/courses/{courseId}` | Delete a course (204) |

### Lessons (nested under Course)
| Method | Path | Description |
|--------|------|-------------|
| GET | `/courses/{courseId}/lessons` | Get all lessons of the course (paginated) |
| POST | `/courses/{courseId}/lessons` | Add a lesson (body: `startTime`, `finishTime`, `dayOfWeek`, `teacherId`) (201) |
| PUT | `/courses/{courseId}/lessons/{lessonId}` | Update a lesson |
| DELETE | `/courses/{courseId}/lessons/{lessonId}` | Delete a lesson (204) |

### Students (nested under Course)
| Method | Path | Description |
|--------|------|-------------|
| GET | `/courses/{courseId}/students` | Get all students of the course (paginated) |
| POST | `/courses/{courseId}/students` | Add a student (body: `{"id": ...}`) (201) |
| DELETE | `/courses/{courseId}/students/{studentId}` | Remove a student from the course (204) |

### Teachers (nested under Course)
| Method | Path | Description |
|--------|------|-------------|
| GET | `/courses/{courseId}/teachers` | Get all teachers of the course (paginated) |
| POST | `/courses/{courseId}/teachers` | Add a teacher (body: `{"id": ...}`) (201) |
| DELETE | `/courses/{courseId}/teachers/{teacherId}` | Remove a teacher from the course (204) |

## Business rules

- Adding a **teacher** to a course requires the user to have the `TEACHER` role.
- Adding a **student** to a course requires the user to have the `STUDENT` role.
- Removing a teacher/student who is **not enrolled** in the course returns a `404`.
- Adding a **lesson** requires the referenced user to be a `TEACHER`.
- All collections are **paginated** (`page`, `size` query params).
- `startTime`/`finishTime` use the `HH:mm` format and `dayOfWeek` the ISO day name (e.g. `Monday`).

## Error responses

Errors are returned in a consistent format (e.g. `400`):

```json
{
  "error": {
    "status": 400,
    "message": "Bad request"
  }
}
```

| Status | Meaning |
|--------|---------|
| 400 | Bad request / invalid role / validation failure |
| 404 | Resource not found |
| 201 | Resource created (POST) |
| 204 | Resource deleted, no content (DELETE) |

## API documentation (Swagger)

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Running the app

1. Create a PostgreSQL database (default config expects `education_center`).
2. Update credentials in `src/main/resources/application.properties` if needed.
3. `spring.jpa.defer-datasource-initialization=true` +
   `spring.sql.init.mode=always` load the `data.sql` seed data after Hibernate
   creates the schema.
4. Run:

```bash
mvn spring-boot:run
```

## Tests

Controller tests are in `src/test/java/fr/_42/educationcenter/controllers/` and cover
GET, POST, PUT and DELETE with **MockMvc** (using H2 as the embedded test database).

```bash
mvn test
```

## Notes

- Spring Data REST is **not** used for this exercise.
- `data.sql` currently seeds the `users` table (5 users: 1 admin, 2 teachers, 2 students).