@file:Suppress("UnstableApiUsage")

pluginManagement {
  repositories {
    gradlePluginPortal()
    mavenCentral()
    mavenLocal()
  }

  resolutionStrategy {
    eachPlugin {
      if (requested.id.id.startsWith("settings.")) {
        useModule("dev.suresh.build:plugins:${extra["test.version"]}")
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

plugins { id("settings.repos") }

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

rootProject.name = "sandbox"
