@file:Suppress("UnstableApiUsage")

import org.gradle.toolchains.foojay.FoojayToolchainResolver
import org.tomlj.*

val versionCatalog: TomlTable? by lazy {
  // A hack to read version-catalog from settings
  runCatching {
        Toml.parse(file("$rootDir/gradle/libs.versions.toml").readText()).getTable("versions")
      }
      .getOrNull()
}

pluginManagement {
  repositories {
    mavenCentral()
    gradlePluginPortal()
  }
}

dependencyResolutionManagement {
  repositories {
    mavenCentral()
    gradlePluginPortal()
  }
  repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
}

// Apply the plugins to all projects
plugins {
  id("org.gradle.toolchains.foojay-resolver")
  id("com.javiersc.semver")
}

toolchainManagement {
  jvm {
    javaRepositories {
      repository("foojay") { resolverClass = FoojayToolchainResolver::class.java }
    }
  }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

enableFeaturePreview("STABLE_CONFIGURATION_CACHE")
