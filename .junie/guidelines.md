# Build Commons Dev Guidelines

This document provides advanced development guidelines for the build-commons project. For basic setup and overview, refer to the main [README.md](../README.md).

## Project Architecture

This is a Gradle composite build with the following structure:

- **gradle/build-logic/**: Included build containing precompiled script plugins and build logic
- **plugins/**: Main plugin modules
  - **plugins/project/**: Project-level plugins (kotlin.jvm, kotlin.mpp, common, etc.)
  - **plugins/settings/**: Settings-level plugins (repos configuration)
  - **plugins/shared/**: Shared utilities and extensions
- **catalog/**: Version catalog module
- **sandbox/**: Testing sandbox project demonstrating plugin usage

## Build Configuration

### Key Gradle Properties

The project uses specific Gradle configurations in `gradle.properties`:

```properties
# Performance optimizations
org.gradle.jvmargs=-Xmx2g
org.gradle.parallel=true
org.gradle.caching=true
org.gradle.configuration-cache=true
org.gradle.configuration-cache.parallel=true

# Kotlin settings
kotlin.code.style=official
kotlin.jvm.target.validation.mode=warning

# Dokka experimental features
org.jetbrains.dokka.experimental.gradle.pluginMode=V2Enabled
org.jetbrains.dokka.experimental.tryK2=true
```

### Version Catalog Integration

The project uses a sophisticated version catalog system (`gradle/libs.versions.toml`) with:
- Centralized dependency versions
- Plugin dependency management
- Custom extension for accessing catalog in precompiled plugins

### JTE (Java Template Engine) Integration

The project uses JTE for code generation:
- Templates in `src/main/jte/`
- Kotlin model generation enabled
- BuildConfig generation for project metadata

## Testing Guidelines

### Test Structure

Tests should follow Kotlin multiplatform conventions:
- **JVM tests**: `src/jvmTest/kotlin/`
- **Common tests**: `src/commonTest/kotlin/`
- **Platform-specific tests**: `src/{platform}Test/kotlin/`

### Testing Framework Configuration

The plugins automatically configure:
- **kotlin-test** for multiplatform testing
- **JUnit 5** for JVM tests
- **Testcontainers** for integration testing
- **kotlinx-coroutines-test** for coroutine testing


### Running Tests

```bash
# Run all tests
./gradlew test

# Run tests for specific module
./gradlew -p sandbox test

# Run tests with specific system properties
./gradlew test -PktorTest -Pk8sTest

# Skip tests during build
./gradlew build -Pskip.test=true
```

### Test Configuration Features

The plugins provide automatic test configuration:
- **JVM target validation** with warning mode
- **Test logging** with configurable events
- **System properties** for test environment (ktorTest, k8sTest)
- **Custom hosts file** support for integration tests
- **Testcontainers BOM** for container-based testing

## Code Style and Formatting

### Kotlin Compiler Options

Standard compiler options are configured:
- **JVM target**: Configurable via version catalog
- **Context parameters**: Enabled for better debugging
- **Opt-ins**: Pre-configured for experimental APIs
- **Assertions**: Disabled for performance in build scripts

### Code Style Guidelines

1. **Use official Kotlin code style** (`kotlin.code.style=official`)
2. **All warnings as errors** in Kotlin DSL (`org.gradle.kotlin.dsl.allWarningsAsErrors=true`)
3. **Consistent formatting** with ktfmt
4. **Explicit API mode** for library modules
5. **Proper package structure** following reverse domain naming

## Plugin Development

### Creating New Plugins

1. **Precompiled Script Plugins**: Place in `plugins/project/src/main/kotlin/`
2. **Class-based Plugins**: Implement in `plugins/project/src/main/kotlin/plugins/`
3. **Settings Plugins**: Place in `plugins/settings/src/main/kotlin/`

### Plugin Registration

Register plugins in `build.gradle.kts`:

```kotlin
gradlePlugin {
  plugins {
    register("My Plugin") {
      id = "dev.suresh.plugin.myplugin"
      implementationClass = "plugins.MyPlugin"
      displayName = "My Plugin"
      description = "Description of my plugin"
      tags = listOf("My Plugin", "build-logic")
    }
  }
}
```

### Accessing Version Catalog in Plugins

Use the catalog hack for accessing versions in precompiled plugins:

```kotlin
dependencies {
  // Hack to access the version catalog from pre-compiled script plugins
  implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}
```

## Build Performance

### Configuration Cache

The project uses Gradle's configuration cache for improved performance:
- **Parallel configuration cache** enabled
- **Problems as warnings** to avoid build failures
- **Stable configuration cache** feature enabled

### Build Optimizations

1. **Parallel execution** enabled
2. **Build caching** enabled
3. **Configuration on demand** enabled
4. **Daemon with 2GB heap** for better performance
5. **Isolated projects** disabled (experimental)

## Semantic Versioning

### Configuration

```properties
semver.tagPrefix=v
semver.project.tagPrefix=v
semver.checkClean=false
semver.commitsMaxCount=100
semver.logOnlyOnRootProject=true
```

### Usage

```bash
# Check current version
./gradlew printSemver

# Create and push version tag
./gradlew pushSemverTag "-Psemver.scope=patch"

# Print next version without creating tag
./gradlew printSemver "-Psemver.scope=minor"
```

## Debugging and Troubleshooting

### Common Issues

1. **Configuration cache problems**: Use `--no-configuration-cache` flag
2. **Memory issues**: Increase heap size in `gradle.properties`
3. **Plugin classpath issues**: Check with `./gradlew buildEnvironment`
4. **Version conflicts**: Use `./gradlew dependencyInsight --dependency <name>`

### Useful Debug Commands

```bash
# Show all tasks
./gradlew tasks --all

# Show project dependencies
./gradlew dependencies

# Show plugin classpath
./gradlew buildEnvironment

# Clean all projects
./gradlew cleanAll

# Check for dependency updates
./gradlew dependencyUpdates --no-configuration-cache
```

## Development Workflow

### Local Development

1. **Make changes** to plugin code
2. **Test in sandbox**: `./gradlew publishToMavenLocal && ./gradlew -p sandbox :build`
3. **Run formatting**: `./gradlew spotlessApply`
4. **Run tests**: `./gradlew test`
5. **Build all**: `./gradlew build`

### Publishing Workflow

1. **Create version tag**: `./gradlew pushSemverTag "-Psemver.scope=patch"`
2. **GitHub Actions** automatically publishes to Maven Central
3. **Verify publication** at https://repo.maven.apache.org/maven2/dev/suresh/build/

## Advanced Features

### BuildConfig Generation

The plugins generate BuildConfig classes with project metadata:
- Project name, version, description
- Git commit information
- Kotlin/Java versions
- Version catalog entries

### Multi-Release JAR Support

The plugins support creating multi-release JARs for different Java versions.

### GraalVM Native Image

Automatic configuration for GraalVM native image compilation with:
- Agent-based configuration
- Custom build arguments
- Resource configuration

### Container Image Building

Integration with Jib for building container images:
- Automatic base image selection
- Optimized layering
- Multi-architecture support

---

*Last updated: 2025-07-13*
