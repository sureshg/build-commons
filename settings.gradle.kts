pluginManagement {
  repositories {
    mavenCentral()
    gradlePluginPortal()
  }
  includeBuild("gradle/build-logic")
}

dependencyResolutionManagement {
  versionCatalogs {
    // Use a new catalog to avoid conflict.
    register("blibs") { from(files("gradle/libs.versions.toml")) }
  }
}

plugins { id("settings.repo") }

rootProject.name = "build-commons"

include("catalog")

include("plugins")
