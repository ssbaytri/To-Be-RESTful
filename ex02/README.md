# Exercise 02: HATEOAS & REST Docs

Extension of **Exercise 01** (Education Center API) reaching the **Level 3** of the
Richardson Maturity Model: the API is now hypermedia-driven (**HATEOAS**) using
**Spring Data REST**, plus **auto-generated documentation** with **Spring REST Docs** + Asciidoctor.

## Added tech

- **Spring Data REST** (`spring-boot-starter-data-rest`) — resources exposed directly from repositories
- **HAL Explorer** (`spring-data-rest-hal-explorer`) — browser for the hypermedia API
- **springdoc-openapi 3.1.1** — Swagger UI now documents the SDR endpoints
- **Spring REST Docs 4.0.1** (`spring-restdocs-mockmvc`, `spring-restdocs-asciidoctor`) — snippets from unit tests
- **asciidoctor-maven-plugin 3.2.0** (AsciidoctorJ 3.0.0) — renders the final HTML doc

## What changed vs. Exercise 01

The hand-written CRUD layer was **removed** (`UsersController`, `CoursesController`,
`UserService`, `CourseService`, the ex00 DTOs and `UsersControllerTest`) and replaced by
Spring Data REST repositories:

| Repository | Exposed at |
|------------|-----------|
| `UserRepository` | `/users` |
| `CourseRepository` | `/courses` |
| `LessonRepository` | `/lessons` |

Pagination, sorting and HAL `_links` are provided by Spring Data REST out of the box.
Authentication, JWT (ex01) and the existing `/signUp` endpoint are kept.

## HATEOAS: publishing a course

Publishing is the custom, hypermedia-driven workflow of this exercise:

1. A course in `DRAFT` state carries an extra `publish` link.
2. `CoursePublishLinkProcessor` (`RepresentationModelProcessor<EntityModel<Course>>`)
   adds the `publish` link **only while the course is `DRAFT`**.
3. `CoursePublishController` (`@RepositoryRestController`) handles
   `POST /courses/{id}/publish` and returns the full HAL resource
   (`assembler.toFullResource(...)`) of the now-`PUBLISHED` course — **without** the
   `publish` link anymore.

| Status | Meaning |
|--------|---------|
| 200 | Course `DRAFT -> PUBLISHED`, HAL resource returned |
| 400 | Course is already published |
| 404 | Course not found |

Course `state` is serialized in uppercase (`DRAFT` / `PUBLISHED`).

## Access rules

| HTTP method | Required role |
|-------------|---------------|
| GET `/`, `/profile`, `/explorer/**`, `/webjars/**` | public |
| POST `/signUp` | public (no token) |
| GET `/**` | any authenticated role |
| POST/PUT/DELETE `/**` | `ADMINISTRATOR` |

`/explorer`, the HAL Explorer UI, is therefore browsable without authentication
(navigation requests are read-only), while every mutation stays admin-only.

## API documentation

- **Swagger UI**: `http://localhost:8080/swagger-ui/index.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`
- **HAL Explorer**: `http://localhost:8080/explorer/`
- **REST Docs (auto-generated)**: `target/generated-docs/index.html` after `mvn package`

The REST Docs page is written by hand in `src/docs/asciidoc/index.adoc` and assembled
from test snippets — the request, response, path parameters and response fields shown
there are **real** examples recorded from the unit test.

## Project structure

```
Education-Center/
├── src/main/java/fr/_42/educationcenter/
│   ├── config/           SecurityConfig (matcher order: public paths first)
│   ├── controllers/      CoursePublishController (POST /courses/{id}/publish)
│   ├── hateoas/          CoursePublishLinkProcessor (adds `publish` link on DRAFT)
│   ├── models/           JPA entities (User, Course, Lesson, UserRole, CourseState)
│   ├── repositories/     Spring Data REST repositories (users/courses/lessons)
│   └── security/         JWT + Spring Security components (from ex01)
├── src/test/java/.../controllers/CoursePublishControllerTest.java
├── src/docs/asciidoc/index.adoc
└── pom.xml
```

## Running the app

1. PostgreSQL database `education_center` (config in `application.properties`).
2. Run:

```bash
mvn spring-boot:run
```

3. Open HAL Explorer (`/explorer`), Swagger UI, or the REST Docs page.

## Tests

`CoursePublishControllerTest` covers the publish workflow: publish success (200 + HAL
resource without the `publish` link), already-published (400), and unauthenticated (403).
It also generates the docs snippets via `.andDo(document("course-publish", ...))`.

```bash
mvn test
```

## Generating the documentation

```bash
mvn package
```

This runs the tests (snippets -> `target/generated-snippets`) and then renders
`target/generated-docs/index.html`. Because output lives in `target/`, a `mvn clean`
removes it — docs are rebuilt on every package.

### Build notes (gotchas discovered)

- **Boot 4 no longer auto-applies `springSecurity()` to the auto-configured MockMvc**,
  so `@WithMockUser` was ignored (tests failed with 403 for everything). The test builds
  MockMvc manually: `MockMvcBuilders.webAppContextSetup(context).apply(documentationConfiguration(provider)).apply(springSecurity()).build()`.
- **spring-restdocs 4.x dropped the `.junit5` package**: the JUnit 5 extension is now
  `org.springframework.restdocs.RestDocumentationExtension` (no `@AutoConfigureRestDocs`
  needed).
- **spring-restdocs-asciidoctor 4.x requires AsciidoctorJ 3.0**, which
  asciidoctor-maven-plugin 3.0.0 did not bundle (2.5.11) — this caused an
  `AbstractMethodError` at the `Preprocessor` API. Fixed by using plugin **3.2.0**
  and pinning `asciidoctorj 3.0.0` as a plugin dependency.
- **springdoc 3.1.0 crashed on SDR schemas** (`Schema.getProperties()` NPE, upstream
  issue #3328) — resolved by upgrading to **3.1.1**.

## Notes

- Reads are authenticated but the HAL navigation UI (`/explorer`) and Swagger are open
  for browsing.
- Passwords remain plain text (out of scope, as in ex01).