@file:Suppress("UnstableApiUsage")

import com.javiersc.semver.project.gradle.plugin.SemverExtension
import org.gradle.toolchains.foojay.FoojayToolchainResolver
import org.tomlj.Toml

val versionCatalog by lazy {
  // A hack to read version catalog from settings
  Toml.parse(file("$rootDir/gradle/libs.versions.toml").readText()).getTable("versions")
      ?: error("Unable to parse the version catalog!")
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

gradle.beforeProject {
  pluginManager.withPlugin("com.javiersc.semver.project") {
    configure<SemverExtension> {
      // Change version to include kotlin version
      // val ktVersion = versionCatalog.getString("kotlin").orEmpty()
      // mapVersion {
      //   it.copy(metadata = ktVersion).toString()
      // }
    }
  }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

enableFeaturePreview("STABLE_CONFIGURATION_CACHE")
