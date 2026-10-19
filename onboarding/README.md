# Spring Boot Onboarding - Days 3 to 5 (Domain, Polymorphism & JUnit Tests)

This module implements core domain logic, object-oriented principles, custom exceptions, and unit testing for the Java & Spring Boot onboarding roadmap.

## 🚀 Features Implemented
1. **Account Domain (Day 3):** Encapsulated state, business invariants (preventing negative initial balances, zero/negative deposits, and overdrafts), and exact decimal arithmetic using `BigDecimal`.
2. **Payment Methods & Polymorphism (Day 4):** A decoupled `PaymentMethod` interface implemented by `CashPayment` and `CardPayment`, utilizing custom checked (`PaymentFailedException`) and unchecked (`InvalidAmountException`) exceptions.
3. **JUnit 5 Unit Tests (Day 5):** Comprehensive unit tests verifying successful operations and edge-case exceptions with 10+ meaningful assertions.

---

## 🛠️ How to Run Tests

Execute all tests using Maven from your terminal:

- **Windows:**
  ```cmd
  mvnw.cmd test