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
$ sdk i java 21.0.4-zulu
$ sdk u java 21.0.4-zulu
```

### Build & Test

```bash
$ ./gradlew build

# To test, run the sample sandbox project by changing the version in `settings.gradle.kts`
$ ./gradlew publishToMavenLocal
$ ./gradlew -p sandbox :build
```

### Publishing

Push a new tag to trigger the [release](https://repo1.maven.org/maven2/dev/suresh/build/) workflow.

   ```bash
   $ git tag -am "v1.0.0 release" v1.0.0
   $ git push origin main --follow-tags
   ```

<details>
<summary>Misc</summary>

### Maven Central

* Publishing

  ```bash
  # Publish to local maven repository
  $ ./gradlew publishToMavenLocal
  $  tree  ~/.m2/repository/dev/suresh/build/

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
