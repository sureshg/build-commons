@file:Suppress("UnstableApiUsage")

import common.*

plugins {
  plugin.kotlin.dsl
  embeddedKotlin("plugin.serialization")
  plugin.publishing
}

description = "Gradle build settings plugins!"

dependencies {
  implementation(projects.plugins.shared)
  implementation(libs.plugins.kotlinx.kover.dep)
  implementation(libs.plugins.gradle.develocity.dep)
  implementation(libs.plugins.semver.dep)
  implementation(libs.plugins.foojay.resolver.dep)
  implementation(libs.tomlj)
}
