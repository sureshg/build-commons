@file:Suppress("UnstableApiUsage")

plugins {
  id("plugin.kotlin-dsl")
  embeddedKotlin("plugin.serialization")
  plugin.publishing
}

description = "Gradle build settings plugins!"

dependencies {
  implementation(projects.plugins.shared)
  implementation(libs.build.kotlinx.kover)
  implementation(libs.build.gradle.develocity)
  implementation(libs.build.semver.plugin)
  implementation(libs.build.foojay.resolver)
  implementation(libs.build.tomlj)
}
