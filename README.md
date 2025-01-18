🐘 Common Build Plugins
----------

[![GitHub Workflow Status][gha_badge]][gha_url]
[![Maven Central Version][maven_img]][maven_url]
[![OpenJDK Version][java_img]][java_url]
[![Kotlin release][kt_img]][kt_url]

Common project and
settings [gradle convention plugin](https://docs.gradle.org/current/samples/sample_convention_plugins.html) to bootstrap
Kotlin JVM & Multiplatform projects.

### Install Java 21

```bash
$ curl -s "https://get.sdkman.io" | bash
$ sdk i java 21.0.5-zulu
$ sdk u java 21.0.5-zulu
```

### Build & Testing

  ```bash
  $ ./gradlew build

  # Check dep updates
  $ ./gradlew dependencyUpdates --no-configuration-cache
  ```

For testing, a separate [sandbox project](/sandbox) is available with the plugin and version catalog applied in
`settings.gradle.kts`. First publish the plugin to the local maven repository and then run the sandbox project.

   ```bash
   $ ./gradlew publishToMavenLocal
   $ ./gradlew -p sandbox :build
   $ ./gradlew -p sandbox :dependencyUpdates --no-configuration-cache
   ```

### Publishing

Push a new tag to trigger the release workflow and publish the plugin
to [maven central](https://repo1.maven.org/maven2/dev/suresh/build/). That's it 🎉.
The next version will be based on the semantic version scope (`major`, `minor`, `patch`)

   ```bash
   $ ./gradlew pushSemverTag "-Psemver.scope=patch"

   # To see the current version
   # ./gradlew v

   # Print the new version
   # ./gradlew printSemver "-Psemver.scope=patch"
   ```

### Published Plugins

| **Gradle Plugin ID**                 | **Version**                                                                                                                                                                                                                                |
|--------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `dev.suresh.plugin.root`             | [![Maven Central](https://img.shields.io/maven-central/v/dev.suresh.plugin.root/dev.suresh.plugin.root.gradle.plugin?logo=gradle&logoColor=white&color=00B4E6)](https://repo1.maven.org/maven2/dev/suresh/plugin/)                         |
| `dev.suresh.plugin.common`           | [![Maven Central](https://img.shields.io/maven-central/v/dev.suresh.plugin.common/dev.suresh.plugin.common.gradle.plugin?logo=gradle&logoColor=white&color=00B4E6)](https://repo1.maven.org/maven2/dev/suresh/plugin/)                     |
| `dev.suresh.plugin.graalvm`          | [![Maven Central](https://img.shields.io/maven-central/v/dev.suresh.plugin.graalvm/dev.suresh.plugin.graalvm.gradle.plugin?logo=gradle&logoColor=white&color=00B4E6)](https://repo1.maven.org/maven2/dev/suresh/plugin/)                   |
| `dev.suresh.plugin.kotlin.jvm`       | [![Maven Central](https://img.shields.io/maven-central/v/dev.suresh.plugin.kotlin.jvm/dev.suresh.plugin.kotlin.jvm.gradle.plugin?logo=gradle&logoColor=white&color=00B4E6)](https://repo1.maven.org/maven2/dev/suresh/plugin/)             |
| `dev.suresh.plugin.kotlin.mpp`       | [![Maven Central](https://img.shields.io/maven-central/v/dev.suresh.plugin.kotlin.mpp/dev.suresh.plugin.kotlin.mpp.gradle.plugin?logo=gradle&logoColor=white&color=00B4E6)](https://repo1.maven.org/maven2/dev/suresh/plugin/)             |
| `dev.suresh.plugin.kotlin.docs`      | [![Maven Central](https://img.shields.io/maven-central/v/dev.suresh.plugin.kotlin.docs/dev.suresh.plugin.kotlin.docs.gradle.plugin?logo=gradle&logoColor=white&color=00B4E6)](https://repo1.maven.org/maven2/dev/suresh/plugin/)           |
| `dev.suresh.plugin.kotlin.benchmark` | [![Maven Central](https://img.shields.io/maven-central/v/dev.suresh.plugin.kotlin.benchmark/dev.suresh.plugin.kotlin.benchmark.gradle.plugin?logo=gradle&logoColor=white&color=00B4E6)](https://repo1.maven.org/maven2/dev/suresh/plugin/) |
| `dev.suresh.plugin.publishing`       | [![Maven Central](https://img.shields.io/maven-central/v/dev.suresh.plugin.publishing/dev.suresh.plugin.publishing.gradle.plugin?logo=gradle&logoColor=white&color=00B4E6)](https://repo1.maven.org/maven2/dev/suresh/plugin/)             |
| `dev.suresh.plugin.repos`            | [![Maven Central](https://img.shields.io/maven-central/v/dev.suresh.plugin.repos/dev.suresh.plugin.repos.gradle.plugin?logo=gradle&logoColor=white&color=00B4E6)](https://repo1.maven.org/maven2/dev/suresh/plugin/)                       |
| `dev.suresh.plugin.catalog`          | [![Maven Central](https://img.shields.io/maven-central/v/dev.suresh.plugin.catalog/dev.suresh.plugin.catalog.gradle.plugin?logo=gradle&logoColor=white&color=00B4E6)](https://repo1.maven.org/maven2/dev/suresh/plugin/)                   |

### Verifying Artifacts

The published artifacts are signed using this [key][signing_key]. The best way to verify artifacts
is [automatically with Gradle][gradle_verification].

[gradle_verification]: https://docs.gradle.org/current/userguide/dependency_verification.html#sec:signature-verification

[signing_key]: https://keyserver.ubuntu.com/pks/lookup?op=get&search=0xc124db3a8ad1c13f7153decdf209c085c8b53ca1


<details>
<summary>Misc</summary>

### Maven Central

* Publishing

  ```bash
  # Publish to local maven repository
  $ rm -rf ~/.m2/repository/dev/suresh
  $ ./gradlew publishToMavenLocal
  $ tree ~/.m2/repository/dev/suresh

  # Publish the plugins to maven central
  $ ./gradlew publishPluginMavenPublicationToMavenCentralRepository

  # Publish the catalog to maven central
  $ ./gradlew :catalog:publishToMavenCentral
  $ ./gradlew :catalog:publishAndReleaseToMavenCentral
  ```

</details>


<!-- Badges -->

[java_url]: https://www.azul.com/downloads/?version=java-21-lts&package=jdk#zulu

[java_img]: https://img.shields.io/badge/OpenJDK-21-e76f00?logo=openjdk&logoColor=e76f00

[kt_url]: https://github.com/JetBrains/kotlin/releases/latest

[kt_img]: https://img.shields.io/github/v/release/Jetbrains/kotlin?include_prereleases&color=7f53ff&label=Kotlin&logo=kotlin&logoColor=7f53ff

[maven_img]: https://img.shields.io/maven-central/v/dev.suresh.build/plugins?logo=gradle&logoColor=white&color=00B4E6

[maven_url]: https://central.sonatype.com/search?namespace=dev.suresh.build

[maven_dl]: https://search.maven.org/remote_content?g=dev.suresh.build&a=plugins&v=LATEST

[gha_url]: https://github.com/sureshg/build-commons/actions/workflows/build.yml

[gha_badge]: https://img.shields.io/github/actions/workflow/status/sureshg/build-commons/build.yml?branch=main&color=green&label=Build&logo=Github-Actions&logoColor=green

[sty_url]: https://kotlinlang.org/docs/coding-conventions.html

[sty_img]: https://img.shields.io/badge/style-Kotlin--Official-40c4ff.svg?style=for-the-badge&logo=kotlin&logoColor=40c4ff
