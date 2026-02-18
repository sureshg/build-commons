# 🐘 Common Build Plugins

[![GitHub Workflow Status][gha_badge]][gha_url]
[![Maven Central Version][maven_img]][maven_url]
[![OpenJDK Version][java_img]][java_url]
[![Kotlin release][kt_img]][kt_url]

Gradle convention [plugins](https://docs.gradle.org/current/samples/sample_convention_plugins.html) that simplify
bootstrapping **Kotlin/Java** projects targeting JVM, Multiplatform (Native/JS/Wasm/Wasi), and GraalVM native-image.
Focus on writing code — these plugins handle the rest:

- **Publishing** — Maven Central, GHCR, container images (Jib), artifact signing
- **Build & Toolchain** — Java/Kotlin toolchain, target platforms (JVM, JS, WASM, WASI, Native), build config generation
- **Coverage, Formatting & ABI** — Code coverage (JVM & KMP), code formatting, binary compatibility (ABI) validation,
  deprecated API scanning (`jdeprscan`)
- **Versioning** — SemVer based on Git tags
- **Testing & Docs** — Testing, reports, JavaDoc, Dokka
- **Benchmarking** — JMH benchmarking via [kotlinx-benchmark](https://github.com/Kotlin/kotlinx-benchmark)
- **Processors** — KSP & annotation processors, GraalVM native-image, executable JARs
- **Version Catalog** — Controls artifact versions and build configurations
- **Auto-configured Dependencies** — `kotlinx-datetime`, `kotlinx-coroutines`, `ktor-client`, `kotlinx-serialization`,
  `kotlinx-io`, logging
- **Auto-configured Compiler Plugins** — `redacted`, `kopy`, `power-assert`, `atomicfu`, `dataframe`

## 🔌 Published Plugins

- `dev.suresh.plugin.root` — Root project configuration and shared settings
- `dev.suresh.plugin.common` — Common conventions for all subprojects
- `dev.suresh.plugin.kotlin.jvm` — Kotlin JVM project setup
- `dev.suresh.plugin.kotlin.mpp` — Kotlin Multiplatform project setup
- `dev.suresh.plugin.graalvm` — GraalVM native-image support
- `dev.suresh.plugin.kotlin.docs` — Documentation generation (Dokka)
- `dev.suresh.plugin.kotlin.benchmark` — JMH benchmarking support
- `dev.suresh.plugin.publishing` — Maven Central publishing and signing
- `dev.suresh.plugin.repos` — Repository configuration (settings plugin)
- `dev.suresh.plugin.catalog` — Version catalog management

## 🚀 How to Use

<details>
<summary><b>1. Configure <code>settings.gradle.kts</code></b></summary>

```kotlin
pluginManagement {
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id.startsWith("dev.suresh.plugin")) {
                useVersion("<plugin version>")
            }
        }
    }

    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins { id("dev.suresh.plugin.repos") }
```

</details>

<details>
<summary><b>2. Apply plugins in <code>build.gradle.kts</code></b></summary>

```kotlin
// Kotlin JVM
plugins {
    id("dev.suresh.plugin.root")
    id("dev.suresh.plugin.kotlin.jvm")
    id("dev.suresh.plugin.publishing")
    // id("dev.suresh.plugin.graalvm")
    application
}

// Kotlin Multiplatform
plugins {
    id("dev.suresh.plugin.root")
    id("dev.suresh.plugin.kotlin.mpp")
    id("dev.suresh.plugin.publishing")
}

kotlin {
    jvmTarget(project)
    jsTarget(project)
    wasmJsTarget(project)
    wasmWasiTarget(project)
    nativeTargets(project) {}
}
```

</details>

<details>
<summary><b>3. Set up the version catalog</b></summary>

Copy [gradle/libs.versions.toml](gradle/libs.versions.toml) and update the project-related metadata
(`group`, `app-mainclass`, etc.).

⚠️ **Don't change** the existing version names in the catalog as they are referenced by the plugins.

</details>

## 🏗️ Build & Test

> [!IMPORTANT]
> Requires Java **25+** (`sdk i java 25.0.1-zulu`) and the
> latest [IntelliJ IDEA](https://www.jetbrains.com/idea/download).

```bash
# Build all modules
$ ./gradlew build

# Update dependencies
$ junie --guidelines-filename=.aiassistant/rules/version-updates.md "Update the dependency versions"
# OR
$ ./gradlew dependencyUpdates --no-parallel
```

A separate [sandbox project](/sandbox) is available for testing with the plugins applied:

```bash
# Publish plugins to Maven local
$ ./gradlew publishToMavenLocal

# Build the sandbox app
$ ./gradlew -p sandbox :build
$ sandbox/build/libs/sandbox

# Show task graph
$ ./gradlew build --task-graph

# Run other plugin tasks
$ ./gradlew -p sandbox :dependencyUpdates --no-parallel

# See the plugin classpath
$ ./gradlew -p sandbox :buildEnvironment | grep -i "dev.suresh"
```

## 📦 Publishing a Release

Push a new tag to trigger the release workflow and publish
to [Maven Central](https://repo.maven.apache.org/maven2/dev/suresh/build/). The next version is based on the semantic
version scope (`major`, `minor`, `patch`).

```bash
$ ./gradlew pushSemverTag "-Psemver.scope=patch"

# See the current version
# $ ./gradlew v

# Print the new version
# $ ./gradlew printSemver "-Psemver.scope=patch"

# For a specific version
# $ git tag -am "v1.2.3 release" v1.2.3
# $ git push origin --tags
```

### Verifying Artifacts

The published artifacts are signed using this [key][signing_key]. The best way to verify artifacts
is [automatically with Gradle][gradle_verification].

[gradle_verification]: https://docs.gradle.org/current/userguide/dependency_verification.html#sec:signature-verification

[signing_key]: https://keyserver.ubuntu.com/pks/lookup?op=get&search=0xc124db3a8ad1c13f7153decdf209c085c8b53ca1

<details>
<summary>Misc</summary>

### Maven Central Publishing

  ```bash
  # Publish to local maven repository
  $ rm -rf ~/.m2/repository/dev/suresh
  $ ./gradlew publishToMavenLocal
  $ tree ~/.m2/repository/dev/suresh

  # Publish the plugins to maven central
  $ ./gradlew publishToMavenCentral
  ```

### Misc

  ```bash
  # Update the Gradle Daemon JVM
  $ ./gradlew updateDaemonJvm --jvm-version=25 --jvm-vendor=adoptium
  ```

</details>

## 📚 References

- [Kotlin DSL Plugin](https://docs.gradle.org/current/userguide/kotlin_dsl.html#sec:kotlin-dsl_plugin)
- [Pre-compiled Script Plugins](https://docs.gradle.org/current/userguide/implementing_gradle_plugins_precompiled.html#sec:applying_external_plugins)

<!-- Badges -->

[java_url]: https://www.azul.com/downloads/?version=java-25-lts&package=jdk#zulu

[java_img]: https://img.shields.io/badge/OpenJDK-25-e76f00?logo=openjdk&logoColor=e76f00

[kt_url]: https://github.com/JetBrains/kotlin/releases/latest

[kt_img]: https://img.shields.io/github/v/release/Jetbrains/kotlin?include_prereleases&color=7f53ff&label=Kotlin&logo=kotlin&logoColor=7f53ff

[maven_img]: https://img.shields.io/maven-central/v/dev.suresh.build/project?logo=gradle&logoColor=white&color=00B4E6

[maven_url]: https://central.sonatype.com/search?namespace=dev.suresh.build

[maven_dl]: https://search.maven.org/remote_content?g=dev.suresh.build&a=plugins&v=LATEST

[plugins_url]: https://repo.maven.apache.org/maven2/dev/suresh/plugin

[gha_url]: https://github.com/sureshg/build-commons/actions/workflows/build.yml

[gha_badge]: https://img.shields.io/github/actions/workflow/status/sureshg/build-commons/build.yml?branch=main&color=green&label=Build&logo=Github-Actions&logoColor=green

[sty_url]: https://kotlinlang.org/docs/coding-conventions.html

[sty_img]: https://img.shields.io/badge/style-Kotlin--Official-40c4ff.svg?style=for-the-badge&logo=kotlin&logoColor=40c4ff

