@file:Suppress("UnstableApiUsage")

// val kotlinVersion =
//    file(rootDir)
//        .resolveSibling("libs.versions.toml")
//        .readLines()
//        .first { it.contains("kotlin") }
//        .split("\"")[1]
//        .trim()

dependencyResolutionManagement {
  repositories {
    mavenCentral()
    gradlePluginPortal()
  }

  versionCatalogs { register("libs") { from(files("../libs.versions.toml")) } }
  repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

rootProject.name = "build-logic"
