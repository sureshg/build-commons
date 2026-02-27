---
name: zen-of-kotlin
description: An idiomatic Kotlin adaptation of the Zen of James, focused on immutable, domain-oriented, type-safe code.
---

# When You Write Kotlin Code

MAKE ILLEGAL STATES UNREPRESENTABLE.
- Model invariants in types so invalid data cannot compile or cannot be constructed.
- When a domain is genuinely closed, sealed hierarchies (`sealed interface` / `sealed class`) can model it clearly; use exhaustive `when` branches when that structure is appropriate.
- Represent optionality explicitly with nullable types (`T?`) and eliminate ambiguity at boundaries.
- Use strongly typed value wrappers (for example `@JvmInline value class`) instead of passing raw primitives everywhere.
- Scope resources correctly (`use { ... }`) so leaks are impossible in normal control flow.

NO MUTABILITY BY DEFAULT.
- Prefer `val` over `var`, and immutable data structures over mutable ones.
- Write pure functions first; keep side effects at the edge of the system.
- Express state changes as new values (`copy(...)`) instead of in-place mutation.
- Isolate unavoidable effects behind small interfaces so behavior stays testable.

MAKE DATA TYPES AND FUNCTIONS DOMAIN-ORIENTED.
- Name types and operations in the language of the domain, not infrastructure.
- Replace primitive obsession with domain-specific types (`CustomerId`, `Email`, `Money`, etc.).
- Parse, then model: convert untrusted input at boundaries into validated domain values.
- Keep domain logic in domain types/functions; keep transport and persistence concerns outside.

PREFER COMPOSITION, EXPRESSIVENESS, AND CLARITY.
- Use small, composable functions and extension functions when they improve readability.
- Use null-safe operators and standard library operations to express intent directly.
- Avoid cleverness that hides behavior; optimize for maintainability and correctness.

TEST PYRAMIDS/ONIONS ARE GOOD. ASSERTION MAGIC IS NOT REQUIRED.
- Favor a layered testing strategy: types and pure functions first, then integrated behavior.
- Keep tests explicit and readable; use straightforward assertions over opaque DSL complexity.
- Test effects through boundaries with fakes first, then real integrations when needed.

USE MODERN KOTLIN, KEEP IT SIMPLE.
- Target Kotlin `2.3.10` and current standard library APIs.
- Prefer the Kotlin standard library before adding dependencies.
- Choose explicit, maintainable code over incidental abstraction.
