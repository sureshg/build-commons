# 🐘 Common Build Plugins

[![GitHub Workflow Status][gha_badge]][gha_url]
[![Maven Central Version][maven_img]][maven_url]
[![OpenJDK Version][java_img]][java_url]
[![Kotlin release][kt_img]][kt_url]

Gradle project and settings [plugins](https://docs.gradle.org/current/samples/sample_convention_plugins.html) that
simplify bootstrapping `Kotlin/Java` projects targeting JVM, Multiplatform (Native/JS/Wasm/Wasi), and GraalVM
native-image. These plugins handle configuring the most common build tasks, including:

* `Maven Central` & `GHCR` publishing for artifacts & container images (`Jib`)
* Code coverage for `JVM` and `Kotlin Multiplatform` projects
* Project versioning (`SemVer`) based on `Git tags`
* Code formatting to enforce consistent code style
* Artifact signing
* Java/Kotlin toolchain configuration
* Target platform (`JVM`, `JS`, `WASM`, `WASI`, `Native`) configuration
* Testing & reports
* `KSP` & annotation processors
* `GraalVM Native` image
* Documentation (`JavaDoc`, `Dokka`)
* Benchmarking (`JMH`)
* Binary compatibility (`ABI`) validation
* Deprecated API scanning (using `jdeprscan`)
* Building truly executable JAR files
* Build configuration generation
* Version catalog to control artifact versions and build configurations
* Automatic configuration of essential dependencies such as:
    * `kotlinx-datetime`
    * `kotlinx-coroutines`
    * `ktor-client`
    * `kotlinx-serialization`
    * `kotlinx-io`
    * `Logging`
* Automatic configuration of `compiler plugins` such as:
    * `redacted`
    * `kopy`
    * `power-assert`
    * `atomicfu`
    * `dataframe`
* And many other common build tasks

These plugins help you focus on writing code, not configuring your build. They provide a solid foundation for your
Kotlin/Java projects, handling the boilerplate and common tasks so you can get started quickly.

## Development Environment Setup

* Install Java 25 or later

  ```bash
  $ curl -s "https://get.sdkman.io" | bash
  $ sdk i java 25.0.1-zulu
  ```

* Import the Gradle project. The initial sync may take some time as it downloads all dependencies.

> [!IMPORTANT]
> For the best and fastest experience, use the latest version of [IntelliJ IDEA](https://www.jetbrains.com/idea/download).
> Upgrade now!

## Build & Testing

  ```bash
  $ ./gradlew build

  # Check dependency updates
  $ caupain

  # OR
  $ ./gradlew dependencyUpdates --no-parallel
  ```

For testing, a separate [sandbox project](/sandbox) is available with the plugin and version catalog applied in
`settings.gradle.kts`. First, publish the plugin to the local Maven repository, then run the sandbox project.

   ```bash
   # Publish the plugins to Maven local
   $ ./gradlew publishToMavenLocal

   # Build the sandbox app using the published plugin
   $ ./gradlew -p sandbox :build
   $ sandbox/build/libs/sandbox

   # Show task graph
   $ ./gradlew build  --task-graph

   # Run other plugin tasks
   $ ./gradlew -p sandbox :dependencyUpdates --no-parallel

   # To see the plugin classpath
   $ ./gradlew -p sandbox :buildEnvironment | grep -i "dev.suresh"
   ```

## Publishing

Push a new tag to trigger the release workflow and publish the plugin
to [Maven Central](https://repo.maven.apache.org/maven2/dev/suresh/build/). That's it! 🎉
The next version will be based on the semantic version scope (`major`, `minor`, `patch`).

   ```bash
   $ ./gradlew pushSemverTag "-Psemver.scope=patch"

   # To see the current version
   # ./gradlew v

   # Print the new version
   # ./gradlew printSemver "-Psemver.scope=patch"

   # For a specific version
   # git tag -am "v1.2.3 release" v1.2.3
   # git push origin --tags
   ```

## Published Plugins

| **Gradle Plugin ID**                 | **Version**                                                                                                                                                                             |
|--------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `dev.suresh.plugin.root`             | [![](https://img.shields.io/maven-central/v/dev.suresh.plugin.root/dev.suresh.plugin.root.gradle.plugin?logo=gradle&logoColor=white&color=00B4E6)][plugins_url]                         |
| `dev.suresh.plugin.common`           | [![](https://img.shields.io/maven-central/v/dev.suresh.plugin.common/dev.suresh.plugin.common.gradle.plugin?logo=gradle&logoColor=white&color=00B4E6)][plugins_url]                     |
| `dev.suresh.plugin.graalvm`          | [![](https://img.shields.io/maven-central/v/dev.suresh.plugin.graalvm/dev.suresh.plugin.graalvm.gradle.plugin?logo=gradle&logoColor=white&color=00B4E6)][plugins_url]                   |
| `dev.suresh.plugin.kotlin.jvm`       | [![](https://img.shields.io/maven-central/v/dev.suresh.plugin.kotlin.jvm/dev.suresh.plugin.kotlin.jvm.gradle.plugin?logo=gradle&logoColor=white&color=00B4E6)][plugins_url]             |
| `dev.suresh.plugin.kotlin.mpp`       | [![](https://img.shields.io/maven-central/v/dev.suresh.plugin.kotlin.mpp/dev.suresh.plugin.kotlin.mpp.gradle.plugin?logo=gradle&logoColor=white&color=00B4E6)][plugins_url]             |
| `dev.suresh.plugin.kotlin.docs`      | [![](https://img.shields.io/maven-central/v/dev.suresh.plugin.kotlin.docs/dev.suresh.plugin.kotlin.docs.gradle.plugin?logo=gradle&logoColor=white&color=00B4E6)][plugins_url]           |
| `dev.suresh.plugin.kotlin.benchmark` | [![](https://img.shields.io/maven-central/v/dev.suresh.plugin.kotlin.benchmark/dev.suresh.plugin.kotlin.benchmark.gradle.plugin?logo=gradle&logoColor=white&color=00B4E6)][plugins_url] |
| `dev.suresh.plugin.publishing`       | [![](https://img.shields.io/maven-central/v/dev.suresh.plugin.publishing/dev.suresh.plugin.publishing.gradle.plugin?logo=gradle&logoColor=white&color=00B4E6)][plugins_url]             |
| `dev.suresh.plugin.repos`            | [![](https://img.shields.io/maven-central/v/dev.suresh.plugin.repos/dev.suresh.plugin.repos.gradle.plugin?logo=gradle&logoColor=white&color=00B4E6)][plugins_url]                       |
| `dev.suresh.plugin.catalog`          | [![](https://img.shields.io/maven-central/v/dev.suresh.plugin.catalog/dev.suresh.plugin.catalog.gradle.plugin?logo=gradle&logoColor=white&color=00B4E6)][plugins_url]                   |

## How to Use

- Apply the following configuration to `settings.gradle.kts` of your project:

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

- Apply the required plugins to your `root` or `sub` project `build.gradle.kts`:

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

- Use the version catalog by copying [gradle/libs.versions.toml](gradle/libs.versions.toml) and changing the
  project-related metadata like `group`, `app-mainclass`, etc.

> [!IMPORTANT]
> **Don't change** the existing version names in the catalog as they are referenced by the plugins.

## Verifying Artifacts

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

## References

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
