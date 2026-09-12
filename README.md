# 🎓 Education Center — Spring Boot Edition

> *"CRUD Grows Up"* — a training-center management API, built progressively
> across three exercises, climbing the **Richardson Maturity Model** from
> hand-rolled CRUD (**Level 2**) all the way to a fully hypermedia-driven,
> JWT-secured, self-documenting REST API (**Level 3**) — built entirely on
> **Spring Boot**.

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.1-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21%20LTS-orange?logo=openjdk)](https://openjdk.org/)
[![Spring Security](https://img.shields.io/badge/Spring%20Security-6-6DB33F?logo=springsecurity&logoColor=white)](https://spring.io/projects/spring-security)
[![PostgreSQL](https://img.shields.io/badge/DB-PostgreSQL-336791?logo=postgresql)](https://www.postgresql.org/)
[![Spring Data REST](https://img.shields.io/badge/Spring%20Data-REST%20%2B%20HATEOAS-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-data-rest)
[![Swagger](https://img.shields.io/badge/API%20Docs-Swagger%20%2F%20OpenAPI-85EA2D?logo=swagger&logoColor=black)](https://swagger.io/)
[![Maven](https://img.shields.io/badge/Build-Maven-C71A36?logo=apachemaven)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/license-Educational-lightgrey)]()

---

## 📖 About

This repository is the **42 School "To be RESTful"** subject, built as a
progressive, three-exercise implementation of the same training-center
domain — one `User` / `Course` / `Lesson` model, reused and re-exposed in
three fundamentally different ways as the requirements climb the maturity
ladder:

| Exercise | Richardson Level | What it proves |
|---|---|---|
| **ex00 — REST API** | Level 2 | Resources + HTTP verbs + CRUD, hand-written, from scratch |
| **ex01 — JWT** | Level 2 + stateless auth | The same API, now behind role-based, DB-free-per-request authentication |
| **ex02 — HATEOAS** *(subject's "Exercice 03")* | Level 3 | The hand-written controller layer is **deleted** and replaced by Spring Data REST — hypermedia becomes the engine of application state |

Each exercise is a genuine architectural shift, not just "more features" —
see [🧠 Key design decisions](#-key-design-decisions) below for what
actually changed under the hood between them, and why.

## 🗂️ Repository structure

```
education-center/
├── ex00/
│   └── Education-Center/   # Hand-written REST API (controllers, services, DTOs)
├── ex01/
│   └── Education-Center/   # + Spring Security: stateless JWT, role-based access
└── ex02/
    └── Education-Center/   # + Spring Data REST, HATEOAS, auto-generated docs
                             #   (subject calls this "Exercice 03" — see note below)
```

Each `Education-Center/` folder is a **complete, independently buildable**
Maven project — later exercises are supersets of earlier ones (same
entities, same DB), not from-scratch rewrites. Every folder ships its own
`README.md`, `application.properties`, and `data.sql`, per the subject's
turn-in requirements.

> **A note on the ex02 / ex03 naming:** the subject PDF's table of
> contents skips straight from *"Exercice 01: JWT"* to *"Exercice 03:
> HATEOAS"* — there is no *Exercice 02* anywhere in the document. The
> folder here is named `ex02` to keep the turn-in directories sequential;
> functionally it *is* the subject's "Exercice 03." The turn-in directory
> itself is left as `ex03/` inside that folder to match the PDF literally.

## ✨ What each exercise adds

| Exercise | Adds |
|---|---|
| **ex00 — REST API** | `User`/`Course`/`Lesson` entities, full CRUD + nested resources (`/courses/{id}/lessons`, `/students`, `/teachers`), pagination on every collection endpoint, Swagger/OpenAPI docs, consistent `{"error": {"status","message"}}` format, MockMvc controller tests |
| **ex01 — JWT** | Spring Security, stateless `POST /signUp` issuing an HS256-signed JWT (user id + role + login as claims), a custom `Authentication` (`JwtAuthenticationToken`), a custom request `Filter` (`JwtAuthenticationFilter`), and a custom `AuthenticationProvider` used only at sign-up — every other request is authorized **without a single database hit** |
| **ex02 — HATEOAS** | Spring Data REST auto-exposing `User`/`Course`/`Lesson` repositories as full HAL CRUD APIs (no controllers, no DTOs), a `POST /courses/{id}/publish` endpoint (`@RepositoryRestController`) driving a `DRAFT → PUBLISHED` state machine, a `RepresentationModelProcessor` that conditionally attaches the `publish` link **only** while a course is a draft, a browsable HAL Explorer, and Spring REST Docs + Asciidoctor generating real API documentation straight from a passing test |

## 🏗️ Architecture

The security layer is identical across ex01 and ex02 — what changes is
*everything downstream of it*:

```
                         ┌────────────────────────────┐
                         │        SecurityConfig       │
                         │  stateless · role matrix ·  │
                         │  /signUp public · no CSRF   │
                         └──────────────┬─────────────┘
                                        │
 Client ──▶ SecurityContextHolderFilter ──▶ JwtAuthenticationFilter ──▶ AuthorizationFilter
        (Authorization: Bearer <jwt>)       (parses claims, no DB)      (checks role vs rule)
                                                                                │
                         ┌──────────────────────────────────────────────────────┴───┐
                         │                                                          │
                  ex00 / ex01 · hand-written                            ex02 · Spring Data REST
                                                                          
            Controller ──▶ Service ──▶ Repository                  Repository ──▶ auto HAL endpoints
           (you write the response shape)                       (framework builds `_links`/`_embedded`)
                         │                                                          │
                         └──────────────────────────┬───────────────────────────────┘
                                                     ▼
                                               PostgreSQL
```

`/signUp` is the **only** place any of these three projects touches the
database to authenticate — every other request trusts the JWT's claims
directly, by design.

## 🔧 Tech stack

| Layer | Technology | Introduced in |
|---|---|---|
| Language / runtime | Java 21 (LTS) | ex00 |
| Framework | Spring Boot 4.1.1 | ex00 |
| Persistence | Spring Data JPA + Hibernate | ex00 |
| Database | PostgreSQL (dev), H2 (tests) | ex00 |
| API docs (hand-written layer) | springdoc-openapi / Swagger UI | ex00 |
| Validation | Jakarta Bean Validation | ex00 |
| Testing | MockMvc, JUnit 5 | ex00 |
| Security | Spring Security 6, stateless JWT (JJWT 0.12.6) | ex01 |
| Hypermedia | Spring Data REST, Spring HATEOAS, HAL Explorer | ex02 |
| Docs generation (auto) | Spring REST Docs 4.0.1 + Asciidoctor Maven Plugin 3.2.0 | ex02 |
| Build | Maven | ex00 |

## 🔑 Entry points

| Path | Where | Description |
|---|---|---|
| `POST /signUp` | ex01, ex02 | Authenticate, receive a JWT (`403` on bad credentials) |
| `/swagger-ui/index.html` | ex00, ex01, ex02 | Interactive, hand-documented API reference |
| `/explorer/` | ex02 only | HAL Explorer — click through the live hypermedia API |
| `target/generated-docs/index.html` | ex02 only, after `mvn package` | Documentation generated from a real, passing test |

Full per-endpoint tables (all CRUD routes, request/response shapes,
business rules) live in each exercise's own `README.md` — this file stays
at the "what's different and why" level on purpose.

## 🧠 Key design decisions

A few choices worth calling out, because they weren't obvious going in:

- **The controller/service/DTO layer from ex00–ex01 was deleted, not
  kept "just in case," going into ex02.** Once `CourseRepository` is
  annotated `@RepositoryRestResource`, Spring Data REST owns `/courses`
  entirely — a hand-written `CoursesController` at the same path doesn't
  coexist with it, it **conflicts** with it. Any service-layer business
  rule (e.g. "only a `TEACHER` can be added as a course teacher") that
  isn't re-expressed as a Spring Data REST hook is simply unreachable
  dead code if left in place.
- **The `publish` link is computed once, centrally, not per-endpoint.**
  `CoursePublishLinkProcessor` implements
  `RepresentationModelProcessor<EntityModel<Course>>`, so *every* place
  Spring Data REST renders a `Course` — a full listing, a single fetch,
  the publish response itself — runs through the same conditional-link
  logic. There's exactly one place that decides "can this course still
  be published," not one check duplicated across several controllers.
- **The publish endpoint uses `PersistentEntityResourceAssembler`, not a
  hand-built response.** This runs the freshly-published `Course` through
  the *same* resource-assembly pipeline Spring Data REST uses internally
  — so a course returned by `POST /courses/{id}/publish` has byte-for-
  byte the same `_links` shape as the same course returned later by a
  plain `GET /courses/{id}`. The API doesn't shape-shift depending on
  which endpoint you hit.
- **Stateless really means stateless.** `JwtAuthenticationFilter` never
  queries `UserRepository` — every claim it needs (id, role, login) was
  already embedded in the token at `/signUp` time. The only two
  `Authentication` objects that ever exist in this codebase
  (`UsernamePasswordAuthenticationToken` at sign-up,
  `JwtAuthenticationToken` on every other request) never overlap in time.
- **`ddl-auto=create-drop` was a deliberate choice, not a default left
  untouched.** With `data.sql` reseeding fixed test data on every start,
  a schema that rebuilds itself from the entities every time removes an
  entire category of "I added a column and now the app won't boot"
  problems — at the cost of not persisting data across restarts, which
  this project never needed to do anyway.

## 🚀 Getting started

Each exercise is self-contained — pick the one you want and its own
`README.md` has exact steps. In general:

### Prerequisites
- Java 21
- PostgreSQL running locally
- Maven (or use the included wrapper, if present)

### Database
```bash
psql -U postgres -c "CREATE DATABASE education_center;"
```
Schema is generated automatically by Hibernate on startup
(`ddl-auto=create-drop`) — `data.sql` then seeds test users, courses and
lessons. No manual schema step required.

### Build & run
```bash
cd ex02/Education-Center      # or ex00 / ex01
mvn spring-boot:run
```

### Seed accounts (`data.sql`)

| Login | Password | Role |
|---|---|---|
| `admin` | `admin123` | `ADMINISTRATOR` |
| `bteacher` | `teacher123` | `TEACHER` |
| `steacher` | `teacher123` | `TEACHER` |
| `astudent` | `student123` | `STUDENT` |
| `bstudent` | `student123` | `STUDENT` |

## 🧪 Tests

Every exercise's controller/endpoint behavior is verified with **MockMvc**
(GET/POST/PUT/DELETE), and ex02 additionally uses that same test to
*generate* its documentation via `.andDo(document(...))` — the docs
can't drift from what the endpoint actually does, because the snippet
only exists if the assertion passed first.

```bash
mvn test        # run tests
mvn package      # run tests + render target/generated-docs/index.html (ex02)
```

## 📝 Notes / known simplifications

Documented deliberately, not discovered accidentally:

- Passwords are stored (and in ex02, serialized) in plain text — out of
  scope for these exercises, called out here rather than left silent.
- In ex02, `User` is serialized directly by Spring Data REST (no DTO
  layer exists anymore) — meaning `GET /users` returns the `password`
  field as-is. Acceptable within this exercise's scope; a real deployment
  would need a projection or `@JsonIgnore` to close that off.
- `data.sql` seeds users in all three exercises; course/lesson seed data
  is only meaningfully exercised in ex00/ex01's fuller dataset.

## 📚 Subject reference

Built against the `To be RESTful — Developing a quality API` subject
(42 School). See each exercise's own `README.md` for its detailed
requirements mapping, and the subject PDF itself for the authoritative
rules — per its own "General Rules" chapter, it is the sole reference;
this repo does not deviate from it except where the document contains
the internal ex02/ex03 numbering gap documented above.

---

*Built as part of the 42 curriculum. Spring Boot 4.1.1, Java 21,
PostgreSQL — three exercises, one growing API.*
