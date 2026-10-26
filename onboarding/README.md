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

📌 Week 2 (Day 9): Maven Lifecycle, Packaging & Logging
Enterprise Logging: Replaced console prints with SLF4J Logger (log.info, log.warn, log.error) across business managers.

Build & Packaging: Executed Maven lifecycle commands (./mvnw clean test package) and verified executable .jar artifact generation under the target/ directory.