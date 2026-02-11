# AGENTS.md

Gradle plugin suite for bootstrapping Kotlin/Java projects targeting JVM, Multiplatform, and GraalVM native-image.

## Project Structure

```
build-commons/
├── catalog/              # Version catalog module
├── plugins/
│   ├── project/          # Project-level plugins
│   ├── settings/         # Settings plugins
│   └── shared/           # Shared plugin utilities
├── sandbox/              # Test project for plugin development
└── gradle/
    ├── build-logic/      # Internal build configuration
    └── libs.versions.toml
```

## Setup

- **Java**: 25+ (install via `sdk i java 25.0.1-zulu`)
- **Build**: Gradle with Kotlin DSL

## Commands

| Command                         | Purpose                   |
|---------------------------------|---------------------------|
| `./gradlew build`               | Build all modules         |
| `./gradlew publishToMavenLocal` | Publish plugins locally   |
| `./gradlew -p sandbox :build`   | Test plugins with sandbox |
| `caupain --no-cache`            | Check dependency updates  |

## Testing

1. Use the sandbox project:

   ```bash
   ./gradlew publishToMavenLocal
   ./gradlew -p sandbox :build
   ```

## Code Style

- Write **idiomatic, concise** Kotlin
- Follow [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Prefer stdlib over third-party libraries
- Avoid excessive scope function nesting (`.let{}`, `.apply{}`)
- Document public APIs with KDoc

## Version Catalog Rules

When updating `gradle/libs.versions.toml`:

- Only modify version values in `[versions]` section
- Preserve exact formatting (quotes, spacing, alignment)
- Never modify `.gradle.kts` files for version changes
- Run `caupain --no-cache` to check for updates

## Key Plugins

| Plugin ID                      | Purpose                    |
|--------------------------------|----------------------------|
| `dev.suresh.plugin.root`       | Root project configuration |
| `dev.suresh.plugin.kotlin.jvm` | Kotlin JVM projects        |
| `dev.suresh.plugin.kotlin.mpp` | Kotlin Multiplatform       |
| `dev.suresh.plugin.publishing` | Maven Central publishing   |
| `dev.suresh.plugin.graalvm`    | GraalVM native-image       |

## PR Guidelines

- Test all changes with the sandbox project before committing
- Keep commits focused and atomic
- Follow [commit message best practices](https://cbea.ms/git-commit/)
- Run `./gradlew build` to verify no regressions

## Do Not

- Add placeholders or TODOs — production-ready code only
- Break existing functionality
- Over-engineer solutions
- Modify version catalog formatting

## Detailed Guidelines

See `.aiassistant/rules/` for comprehensive instructions:

| File                 | Purpose                                         |
|----------------------|-------------------------------------------------|
| `kotlin.md`          | Kotlin coding standards, multiplatform patterns |
| `version-updates.md` | Version catalog update process                  |
| `grammar.md`         | Writing and grammar style guidelines            |
