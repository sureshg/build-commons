pluginManagement {
  repositories {
    mavenCentral()
    gradlePluginPortal()
  }
  includeBuild("gradle/build-logic")
}

plugins { id("settings.repos") }

rootProject.name = "build-commons"

include("plugins")

include("catalog")
