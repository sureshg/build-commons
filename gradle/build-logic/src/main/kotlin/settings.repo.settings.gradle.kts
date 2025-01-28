@file:Suppress("UnstableApiUsage")

import com.javiersc.semver.settings.gradle.plugin.SemverSettingsExtension
import org.gradle.toolchains.foojay.FoojayToolchainResolver
import org.tomlj.Toml

val versionCatalog by lazy {
  // A hack to read version catalog from settings
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

configure<SemverSettingsExtension> {
  // val ktVersion = versionCatalog.getString("kotlin").orEmpty()
  // mapVersion { it.copy(metadata = ktVersion).toString() }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

enableFeaturePreview("STABLE_CONFIGURATION_CACHE")
