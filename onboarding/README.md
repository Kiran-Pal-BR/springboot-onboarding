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
com.example.onboarding
├── domain
│   ├── account
│   │   └── Account.java
│   └── payment
│       ├── PaymentMethod.java
│       ├── CashPayment.java
│       └── CardPayment.java
├── contact
│   ├── Contact.java                  // Immutable Record
│   ├── ContactManager.java           // In-memory Map store with O(1) lookups & Streams
│   └── exception
│       ├── ContactNotFoundException.java
│       └── DuplicateContactException.java
├── expense
│   ├── Expense.java                  // Immutable Record
│   ├── ExpenseCategory.java          // Type-safe enum
│   └── ExpenseManager.java           // Expense summary & date filters
└── exception
    ├── InsufficientBalanceException.java
    ├── InvalidAmountException.java
    └── PaymentFailedException.java




Milestones & Progress (Days 1 – 9)
📌 Week 1 (Days 3–5): Domain Modeling & Polymorphism
Account Domain: Built robust business invariants (no negative initial balances or withdrawals exceeding funds) using BigDecimal for precision.

Payment Methods: Implemented polymorphism using the PaymentMethod interface (CashPayment, CardPayment) following the Open/Closed Principle (OCP).

Exceptions: Designed custom checked (PaymentFailedException) and unchecked (InsufficientBalanceException, InvalidAmountException) exception contracts.

Unit Testing: Created 10+ meaningful assertions using JUnit 5.


Week 2 (Days 6–7): In-Memory Contact Manager & StreamsCollections & Immutability: Built an in-memory ContactManager using HashMap for $O(1)$ lookups and uniqueness enforcement. 
Used Java record types for thread-safe immutability.Streams & Optional: Integrated modern Java functional programming (.filter(), .map(), .reduce(), .toList()) and 
Optional<Contact> to prevent NullPointerException.

Week 2 (Day 8): Expense Tracker & Summary Module
Temporal Data & Enums: Used java.time.LocalDate for date-range filtering and ExpenseCategory enums for type-safe categorization.

Aggregation: Computed total expenses and category-wise breakdowns using Stream reduction.

 Week 2 (Day 9): Maven Lifecycle, Packaging & Logging
Enterprise Logging: Replaced console prints with SLF4J Logger (log.info, log.warn, log.error) across business managers.

Build & Packaging: Executed Maven lifecycle commands (./mvnw clean test package) and verified executable .jar artifact generation under the target/ directory.

Week 2 (Day 10): Java Review, Exceptions, Debugging & Tooling
Debugging & Tooling: Mastered IDE-based debugging, Maven dependency management, and bytecode/JDK configuration resolving Java 21 compilation targets.

Week 3 (Days 11–12): Spring Boot Bootstrapping & REST APIs
Spring Boot Bootstrapping: Configured Spring Boot 3.3.0 application lifecycle and embedded server setup (OnboardingApplication.java).

System Health Diagnostics: Implemented HealthController (/api/health) to monitor application availability.

In-Memory Tasks API (TaskController):

@RestController & @RequestMapping("/api/tasks") configuration.

GET /api/tasks: List all tasks with optional query parameter filtering (?completed=true/false).

GET /api/tasks/{id}: Retrieve specific tasks using @PathVariable.

POST /api/tasks: Create tasks using @RequestBody mapping to immutable Task records.



Day 13: Domain Models & Basic CRUD
Setup project structure and domain records.

Implemented in-memory thread-safe storage using ConcurrentHashMap.

Initialized basic GET (with query parameter filtering) and POST endpoints.

Day 14: Full CRUD & HTTP Status Codes
Implemented PUT (full update) and DELETE (removal) operations.

Developed a flexible PATCH partial update mechanism supporting title, description, and completed fields via a dynamic Map<String, Object> request body.

Configured precise HTTP status codes (201 Created, 204 No Content, 400 Bad Request, 404 Not Found).

Day 15: Production-Ready Error Handling & Configuration
Migrated configuration from application.properties to hierarchical application.yml.

Implemented centralized exception handling using @RestControllerAdvice to catch validation errors and unexpected exceptions globally.

Standardized error responses using a consistent ErrorResponseDto payload (timestamp, status, error, message, path).
# Task Management Onboarding API (Spring Boot 3.3.0)


Day 16: Refactoring to Layered Architecture & DTOs

Structured the project into standard architectural layers: Controller, Service, Repository, and DTOs.

Implemented Bean Validation (@Valid, @NotBlank) on input records (TaskCreateRequestDto, TaskUpdateRequestDto).

Decoupled internal data models from client-facing responses using TaskResponseDto.






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