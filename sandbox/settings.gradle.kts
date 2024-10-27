@file:Suppress("UnstableApiUsage")

pluginManagement {
  repositories {
    gradlePluginPortal()
    mavenLocal()
    mavenCentral()
  }

  // val testVersion = extra["test.version"].toString()
  //
  // 1. Use individual plugin marker artifact
  //
  // plugins {
  //   listOf("repos", "root", "kotlin.jvm", "kotlin.mpp").forEach {
  //     id("dev.suresh.plugin.$it") version testVersion
  //   }
  // }

  // 2. Or directly use the main plugin artifact
  //
  // resolutionStrategy {
  //   eachPlugin {
  //     if (requested.id.id.startsWith("dev.suresh.plugin")) {
  //       useModule("dev.suresh.build:plugins:$testVersion")
  //     }
  //   }
  // }
}

dependencyResolutionManagement {
  repositories {
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()
  }

  repositoriesMode = RepositoriesMode.PREFER_SETTINGS

  versionCatalogs {
    create("libs") {
      from("dev.suresh.build:catalog:+")
      version("java", "21")
    }
  }
}

plugins { id("dev.suresh.plugin.repos") version "+" }

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

rootProject.name = "sandbox"
