#  Spring Boot Onboarding Roadmap

Welcome to my Spring Boot & Java backend onboarding repository! This project documents my structured journey from core Java domain modeling to enterprise-grade Spring Boot architecture.

---

##  Tech Stack & Tools
* **Language:** Java 17 / Java 21
* **Build Tool:** Apache Maven (`./mvnw`)
* **Testing:** JUnit 5 (Assertions, `@BeforeEach`, Exceptions)
* **Logging:** SLF4J / Logback
* **Key Java Features:** `BigDecimal` (Financial accuracy), `java.time.LocalDate`, Records (Immutability), Collections (`HashMap`, `List`), Streams API, and `Optional`.

---

## 📂 Project Package Hierarchy
```text
src
└── main
    ├── java
    │   └── com.example.onboarding
    │       ├── contact
    │       │   ├── exception
    │       │   │   ├── ContactNotFoundException.java
    │       │   │   └── DuplicateContactException.java
    │       │   ├── Contact.java
    │       │   └── ContactManager.java
    │       ├── controller
    │       │   ├── HealthController.java
    │       │   ├── ProjectController.java
    │       │   └── TaskController.java
    │       ├── domain
    │       │   ├── account
    │       │   │   └── Account.java
    │       │   ├── exception
    │       │   │   ├── InsufficientBalanceException.java
    │       │   │   ├── InvalidAmountException.java
    │       │   │   └── PaymentFailedException.java
    │       │   └── payment
    │       │       ├── CardPayment.java
    │       │       ├── CashPayment.java
    │       │       └── PaymentMethod.java
    │       ├── exception
    │       │   ├── DuplicateResourceException.java
    │       │   ├── GlobalExceptionHandler.java
    │       │   └── ProjectNotFoundException.java
    │       ├── expense
    │       │   ├── Expense.java
    │       │   ├── ExpenseCategory.java
    │       │   └── ExpenseManager.java
    │       ├── project
    │       │   ├── dto
    │       │   │   ├── ErrorResponseDto.java
    │       │   │   ├── ProjectCreateRequestDto.java
    │       │   │   ├── ProjectResponseDto.java
    │       │   │   └── ProjectUpdateRequestDto.java
    │       │   ├── Project.java
    │       │   ├── ProjectRepository.java
    │       │   └── ProjectService.java
    │       └── task
    │           ├── dto
    │           │   ├── ErrorResponseDto.java
    │           │   ├── TaskCreateRequestDto.java
    │           │   ├── TaskResponseDto.java
    │           │   └── TaskUpdateRequestDto.java
    │           ├── Task.java
    │           ├── TaskRepository.java
    │           └── TaskService.java
    └── resources
        └── db
            └── migration
                ├── V1__init_schema.sql
                ├── V2__seed_dev_data.sql
                ├── V3__add_project_created_at.sql
                └── V4__replace_seed_project_id_with_uuid.sql```




Milestones & Progress 
📌 Week 1 (Days 3–5): Domain Modeling & Polymorphism
- Account Domain: Built robust business invariants (no negative initial balances or withdrawals exceeding funds) using BigDecimal for precision.
- Payment Methods: Implemented polymorphism using the PaymentMethod interface (CashPayment, CardPayment) following the Open/Closed Principle (OCP).
- Exceptions: Designed custom checked (PaymentFailedException) and unchecked (InsufficientBalanceException, InvalidAmountException) exception contracts.
- Unit Testing: Created 10+ meaningful assertions using JUnit 5.

===============================================================================================================================================================================================
Week 2 (Days 6–7): In-Memory Contact Manager & StreamsCollections & Immutability: Built an in-memory ContactManager using HashMap for $O(1)$ lookups and uniqueness enforcement. 
- Used Java record types for thread-safe immutability.Streams & Optional: Integrated modern Java functional programming (.filter(), .map(), .reduce(), .toList()) and 
- Optional<Contact> to prevent NullPointerException.

Week 2 (Day 8): Expense Tracker & Summary Module
- Temporal Data & Enums: Used java.time.LocalDate for date-range filtering and ExpenseCategory enums for type-safe categorization.
- Aggregation: Computed total expenses and category-wise breakdowns using Stream reduction.

 Week 2 (Day 9): Maven Lifecycle, Packaging & Logging
- Enterprise Logging: Replaced console prints with SLF4J Logger (log.info, log.warn, log.error) across business managers.

- Build & Packaging: Executed Maven lifecycle commands (./mvnw clean test package) and verified executable .jar artifact generation under the target/ directory.

Week 2 (Day 10): Java Review, Exceptions, Debugging & Tooling
- Debugging & Tooling: Mastered IDE-based debugging, Maven dependency management, and bytecode/JDK configuration resolving Java 21 compilation targets.

====================================================================================================================================================================================================
Week 3 (Days 11–12): Spring Boot Bootstrapping & REST APIs
- Spring Boot Bootstrapping: Configured Spring Boot 3.3.0 application lifecycle and embedded server setup (OnboardingApplication.java).

- System Health Diagnostics: Implemented HealthController (/api/health) to monitor application availability.
In-Memory Tasks API (TaskController):

@RestController & @RequestMapping("/api/tasks") configuration.

GET /api/tasks: List all tasks with optional query parameter filtering (?completed=true/false).

GET /api/tasks/{id}: Retrieve specific tasks using @PathVariable.

POST /api/tasks: Create tasks using @RequestBody mapping to immutable Task records.



Week 3 (Day 13): Domain Models & Basic CRUD
- Setup project structure and domain records.
- Implemented in-memory thread-safe storage using ConcurrentHashMap.
- Initialized basic GET (with query parameter filtering) and POST endpoints.


Week 3 (Day 14): Full CRUD & HTTP Status Codes
- Implemented PUT (full update) and DELETE (removal) operations.
- Developed a flexible PATCH partial update mechanism supporting title, description, and completed fields via a dynamic Map<String, Object> request body.
- Configured precise HTTP status codes (201 Created, 204 No Content, 400 Bad Request, 404 Not Found).


Week 3 (Day 15): Production-Ready Error Handling & Configuration
- Migrated configuration from application.properties to hierarchical application.yml.
- Implemented centralized exception handling using @RestControllerAdvice to catch validation errors and unexpected exceptions globally.
- Standardized error responses using a consistent ErrorResponseDto payload (timestamp, status, error, message, path).

=======================================================================================================================================================================================================

Week 4 (Day 16): Refactoring to Layered Architecture & DTOs
- Structured the project into standard architectural layers: Controller, Service, Repository, and DTOs.
- Implemented Bean Validation (@Valid, @NotBlank) on input records (TaskCreateRequestDto, TaskUpdateRequestDto).
- Decoupled internal data models from client-facing responses using TaskResponseDto.


Week 4 (Day 17): Spring Data JPA & Database Persistence
- Integrated Spring Data JPA and PostgreSQL database connectivity (`spring-boot-starter-data-jpa`, `postgresql` driver).
- Converted domain models into `@Entity` classes (`Project` and `Task`) with proper JPA annotations and relational mappings (`@ManyToOne`, `@OneToMany`).
- Created Spring Data repositories (`ProjectRepository`, `TaskRepository`) to abstract database operations, replacing in-memory ConcurrentHashMaps.

Week 4 (Day 18): Advanced Relationships, Filtering & Zero Entity Leakage
- Implemented robust relational query filtering between Projects and Tasks.
- Enforced strict Zero Entity Leakage by mapping database entities directly to isolated DTOs (`ProjectResponseDto`, `TaskResponseDto`) in the service and controller layers.
- Resolved N+1 query considerations and ensured clean separation of concerns across architectural layers.

Week 4 (Day 19): Production-Grade Database Version Control (Flyway)
- Migrated from Hibernate's automatic schema updates (`ddl-auto=update`) to production-grade validation (`ddl-auto=validate`).
- Configured **Flyway Versioned Migrations** under `src/main/resources/db/migration/`.
- Created structured versioned SQL scripts:

Week 4 (Day 20):@Transactional, pagination/sorting, uniqueness/conflict semantics.
- Upgraded service and repository layers to support Pagination and Sorting using Pageable and Page<T> in both TaskService and ProjectService
- Enforced strict business resource uniqueness constraints (existsByTitle for tasks and existsByName for projects) to prevent duplicate record creation.
- Mapped custom DuplicateResourceException to standard HTTP 409 Conflict status codes via @RestControllerAdvice.
- Implemented declarative Transactional Integrity (@Transactional) at the service layer to guarantee automatic rollbacks upon unexpected runtime exceptions.

=====================================================================================================================================================================================================================
Week 5 (Day 21): Testing pyramid; pure unit tests; Mockito and JUnit.
- Implemented the foundation of the Testing Pyramid by building fast, isolated pure unit tests using JUnit 5 and Mockito.
- Configured Mockito mocks for database repositories (TaskRepository and ProjectRepository) to execute service-layer tests without touching a real database.
- Covered comprehensive success and rejected/error scenarios in TaskServiceTest and ProjectServiceTest



Week 5 (Day 22): PI tests for statuses and validation; reproducible DB integration test.
1) Web Layer Tests(Controllers) : 
- Implemented 'ProjectControllerTest' and 'TaskControllerTest' using '@WebMvcTest'
- Verified HTTP request mappings, request/response validation, and JSON serialization/deserialization.

2) Database Intergration Tests (Repositories) : 
- Created `ProjectRepositoryTest` and `TaskRepositoryTest` using `@DataJpaTest` and `TestEntityManager`.
- Tested custom JPA queries (such as `findByIdWithTasks` and `findAllWithTasks`) designed to prevent **N+1 query problems**.
- Validated pagination support (`Page<Task>`) and custom lookup queries (`existsByName`, `existsByTitle`).



Week 5 (Day 23): Spring Security Basics & Authorization
 **Key Learnings:** 
- Understood the core distinction between **Authentication** (who you are) and **Authorization** (what you are allowed to do).
- Explored password hashing principles and the principle of least privilege.
- Configured Spring Security's `SecurityFilterChain` to intercept HTTP requests.

**Implementation:** 
- Added initial protected and public endpoints using a learning-only setup without hard-coded custom token cryptography.


Week 5  (Day 24): OAuth2 Resource Server & JWT Validation
 **Key Learnings:** 
- Mastered OAuth2 Resource Server concepts and JSON Web Token (JWT) verification mechanics.
- Explored custom claims mapping (extracting roles from nested structures like Keycloak's `realm_access.roles`).
  * Managed secure secrets and configurations using environment variables.
  
 **Implementation:** 
- Configured Spring Security OAuth2 Resource Server with JWT support.
- Documented the external IdP (Keycloak) workflow where users authenticate against the IdP, receive a signed JWT, and the API validates the token via local decoding or JWK set matching.


Week 5 (Day 25): Production Readiness, Observability & Documentation

 **Key Learnings:** 
- Learned enterprise code review checklists, standard SLF4J logging levels (`DEBUG`, `INFO`, `WARN`, `ERROR`), and basic OWASP security hygiene (zero hard-coded secrets, input validation).
- Implemented system health monitoring and auto-generated API documentation.

 **Implementation & Verification:** 
- Spring Boot Actuator: Integrated `spring-boot-starter-actuator` to expose deep health checks and metadata (`/actuator/health`, `/actuator/info`).
- OpenAPI & Swagger UI: Configured `springdoc-openapi` with professional `@OpenAPIDefinition` metadata, allowing interactive API testing via `/swagger-ui/index.html`.
- Security Hardening: Configured public access rules for monitoring and documentation endpoints while locking down business and admin routes behind JWT and RBAC.
- Observability: Added enterprise logging in `OnboardingApplication` to track application lifecycle and startup events.

Week 6 (Day 26): Front-end integration.
- CORS Setup: Configured WebConfig.java to allow secure communication between Nuxt (localhost:3000) and Spring Boot (localhost:8080).
- Authentication Flow: Implemented Resource Owner Password Credentials (ROPC) flow via Keycloak to acquire JWT access tokens.
- Initial Browser UI: Rendered initial list views for Projects and Tasks in a clean Nuxt 3 dashboard page (pages/index.vue).

Week 6 (Day 27): Forms, Validation, CUD UX, Filtering, & Pagination
- Forms &  UX: Built interactive forms for creating new Projects and Tasks, alongside delete actions with immediate UI synchronization.
- Client & Server Validation: Integrated @Valid annotations on Spring DTOs (ProjectCreateRequestDto, TaskCreateRequestDto) and displayed server-side validation error banners on the frontend.
- Pagination Support: Handled Spring Data Page<T> responses via .content extraction and built interactive pagination controls (Next/Previous pages) for both Projects and Tasks.
- Filtering: Added status-based filtering (completed=true/false) for Tasks in the dashboard interface.

###########################################################################################################

## 🚀 API Endpoints

| Method | Endpoint | Description | Status Codes |
| :--- | :--- | :--- | :--- |
| **GET** | `/api/tasks` | Get all tasks (Supports `?completed=true/false` filter) | `200 OK` |
| **GET** | `/api/tasks/{id}` | Get task by UUID | `200 OK`, `404 Not Found` |
| **POST** | `/api/tasks` | Create a new task (Validated via `@Valid`) | `201 Created`, `400 Bad Request` |
| **PUT** | `/api/tasks/{id}` | Fully update an existing task | `200 OK`, `400 Bad Request`, `404 Not Found` |
| **PATCH** | `/api/tasks/{id}` | Partially update task fields (`title`, `description`, `completed`) | `200 OK`, `400 Bad Request`, `404 Not Found` |
| **DELETE**| `/api/tasks/{id}` | Delete a task by ID | `204 No Content`, `404 Not Found` |
