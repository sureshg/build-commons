pluginManagement {
  repositories {
    mavenCentral()
    gradlePluginPortal()
  }
  includeBuild("gradle/build-logic")
}

plugins { id("settings.repo") }

rootProject.name = "build-commons"

include("catalog")

include("plugins:shared")

include("plugins:project")

include("plugins:settings")
