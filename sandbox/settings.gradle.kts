@file:Suppress("UnstableApiUsage")

pluginManagement {
  repositories {
    gradlePluginPortal()
    mavenCentral()
    mavenLocal()
  }

  val testVersion = extra["test.version"].toString()
  plugins {
    listOf("repos", "root", "kotlin.jvm", "kotlin.mpp").forEach {
      // Use individual plugin marker artifact
      // id("dev.suresh.plugin.$it") version testVersion
    }
  }

  resolutionStrategy {
    eachPlugin {
      if (requested.id.id.startsWith("dev.suresh.plugin")) {
        // Or directly use the plugin artifact
        useModule("dev.suresh.build:plugins:$testVersion")
      }
    }
  }
}

dependencyResolutionManagement {
  repositories {
    mavenCentral()
    gradlePluginPortal()
    mavenLocal()
  }
  repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS

  versionCatalogs {
    create("mylibs") {
      from("dev.suresh.build:catalog:${extra["test.version"]}")
      version("java", "21")
    }
  }
}

plugins { id("dev.suresh.plugin.repos") }

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

rootProject.name = "sandbox"
