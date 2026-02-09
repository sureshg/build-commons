---
apply: always
---

# Kotlin Project Rules

## 1. Build & Dependencies

### Amper Build System

- Always use Amper — never Gradle build scripts
- Add `src@target` only when platform-specific code is truly necessary
- For JVM-only: single JVM target, no `expect`/`actual`
- Config
  reference: [common.module-template.yaml](https://github.com/sureshg/kmp-amper/blob/main/shared/common.module-template.yaml), [module.yaml](https://github.com/sureshg/kmp-amper/blob/main/shared/module.yaml)
- Docs: [amper.org/dev](https://amper.org/dev/)

### Versions & Dependencies

- **Kotlin**: latest (check `libs.versions.toml`)
- **Java target**: 25 (`--enable-preview` when needed)
- **Platform**: Kotlin Multiplatform by default (JVM, Native, JS, Wasm)
- Always check `libs.versions.toml` for existing references before adding dependencies
- Prefer Kotlin/Java stdlib and latest idioms before third-party libraries
- Never use Guava, Apache Commons, or heavy Java utility libraries
- Use latest stable versions

### Core Libraries (Prefer These First)

- `kotlinx-coroutines` — async/concurrency
- `kotlinx-serialization` — JSON/CBOR/Protobuf
- `kotlinx-datetime` — date/time
- `kotlinx-io` — I/O and byte buffers
- `ktor-client` — HTTP (`ktor-client-java` engine for JVM-only)
- `kotlin-logging` — logging (`io.github.oshai:kotlin-logging`)
- **JVM-only**: prefer JDK APIs (`java.net.http.HttpClient`, `java.security`, `javax.crypto`) when multiplatform
  portability isn't needed

## 2. Language & Style

### Idiomatic Kotlin

- Write **clean, clear, concise, idiomatic** Kotlin — #1 priority
- Functional style: `map`, `filter`, `fold`, `buildList`
- `data class` over manual `toString`/`hashCode`/`equals`
- Default parameter values over builder pattern or overloads
- Composition over inheritance
- `sealed class`/`sealed interface` for restricted hierarchies — but don't over-abstract; `enum class` is often enough,
  and sometimes no abstraction is best
- `require`/`check`/`error` for preconditions
- `when` expressions over `if-else` chains
- Destructuring declarations where they improve readability
- Simple `try-catch` is often clearer than wrapping everything in `Result` — use the right tool for the situation
- Use context parameters judiciously where they genuinely reduce boilerplate
- Don't over-abstract — use abstractions only when they provide clear value and safety

### Null Safety

- Leverage null safety fully — avoid `!!` except in tests
- Use `?.let {}`, `?:`, and safe calls idiomatically
- Prefer non-nullable types; make nullability explicit
- `Nothing` return type for functions that always throw

### Scope Functions & Extensions

- Don't overuse scope functions — avoid nested `.let{}`, `.run{}`, `.apply{}` chains
- A simple `if`/`val` is often clearer than `.let { }`
- Extension functions only when reused in multiple places — sometimes an inline helper or regular function is better
- Group related extensions in `Extensions.kt` per module

### File Organization

- Don't create excessive files — consolidate related code
- Remove unnecessary abstractions, interfaces, and wrappers
- One file can contain multiple related classes/functions
- Minimal file count with clear organization

## 3. Concurrency

### Coroutines & Virtual Threads

- Use structured concurrency: `coroutineScope`, `supervisorScope`
- **JVM-only**: use a `newVirtualThreadPerTaskExecutor`-based dispatcher — virtual threads can block freely, avoiding
  unnecessary dispatcher switching
- Use `suspend` functions when the operation is naturally async or needs cancellation support; for simple blocking JVM
  calls on virtual threads, direct calls are fine
- Prefer `Flow` over callbacks or reactive streams
- Use `Mutex` and `Channel` from kotlinx-coroutines, not Java locks
- Don't wrap every blocking call in `withContext(Dispatchers.IO)` when already on a virtual-thread dispatcher

## 4. Multiplatform

- Default to multiplatform targets unless otherwise stated
- Common code first, platform code only when necessary
- Even for JVM-only, prefer kotlinx libraries (`coroutines`, `serialization`, `datetime`, `kotlinx-io`, `ktor`,
  `kotlin-logging`) for future multiplatform migration
- JVM-only: stick to JVM target structure, no unnecessary `expect`/`actual`

## 5. Code Conversion (Java → Kotlin)

### Approach

- **Never file-by-file** — consolidate into clean, idiomatic Kotlin, removing unnecessary abstractions
- Don't follow old Java/J2EE patterns — simplify aggressively
- **Zero bugs, zero functionality changes** — exactly equivalent
- **No performance regressions** — improve where possible

### Specifics

- `Enumeration`/`Iterator` → Kotlin collections
- `kotlinx-io` buffers for byte manipulation (unless using Java FFM)
- Replace Guava/Apache Commons with stdlib
- `data class`, `sealed class`, `enum class` over verbose Java patterns
- `object` for singletons, not static utility classes
- Preserve important existing documentation in migrated code
- KDoc only where meaningful — skip trivial getters/setters

## 6. Testing

- Port **all tests** with the same coverage and standards
- `kotlin.test` for multiplatform, JUnit 5+ for JVM-only
- `kotlinx-coroutines-test` for coroutine testing (`runTest`, `TestDispatcher`)
- `mockk` only when truly necessary — prefer fakes and test doubles
- `kotest` assertions for richer matchers
- Use `@TempDir`, parameterized tests, and JUnit 5 extensions where appropriate

## 7. Performance

- Use `Sequence` only for genuinely large collections — don't complicate simple collection chains
- `inline` for lambdas in hot paths
- Avoid unnecessary allocations in tight loops
- `@JvmStatic`, `@JvmField`, `const val` for JVM interop
- Profile before optimizing — no premature micro-optimization

## 8. Documentation

- KDoc for public APIs and non-obvious logic only
- Don't document obvious code
- `@param`, `@return`, `@throws` only when not self-evident
- Self-documenting code through clear naming

## Reference

- [Kotlin Docs](https://kotlinlang.org/docs/home.html) · [API](https://kotlinlang.org/api/latest/jvm/stdlib/) · [Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- [Kotlinx Coroutines](https://github.com/Kotlin/kotlinx.coroutines) · [Serialization](https://github.com/Kotlin/kotlinx.serialization) · [I/O](https://github.com/Kotlin/kotlinx-io)
- [Ktor](https://ktor.io/docs/welcome.html) · [Amper](https://amper.org/dev/)
- [Java 25 API](https://docs.oracle.com/en/java/javase/25/docs/api/index.html) · [Core Libs](https://docs.oracle.com/en/java/javase/25/core/java-core-libraries1.html) · [Language Changes](https://docs.oracle.com/en/java/javase/25/language/java-language-changes-release.html) · [Dev Guide](https://dev.java/learn/)
