@file:Suppress("UnstableApiUsage")

dependencyResolutionManagement {
  repositories {
    mavenCentral()
    gradlePluginPortal()
  }

  versionCatalogs { register("libs") { from(files("../gradle/libs.versions.toml")) } }
  repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

rootProject.name = "plugins"
