@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.dsl.*

plugins {
  id("org.gradle.kotlin.kotlin-dsl")
  embeddedKotlin("plugin.serialization")
  com.github.`ben-manes`.versions
  com.diffplug.spotless
  plugin.publishing
}

description = "Shared module for build plugins!"

// Java version used for Kotlin Gradle precompiled script plugins.
val dslJavaVersion = libs.versions.kotlin.dsl.jvmtarget

kotlin {
  compilerOptions {
    jvmTarget = dslJavaVersion.map(JvmTarget::fromTarget)
    freeCompilerArgs.addAll(
        "-Xjdk-release=${dslJavaVersion.get()}",
        "-Xno-param-assertions",
        "-Xno-call-assertions",
        "-Xno-receiver-assertions")
    optIn.addAll(
        "kotlin.ExperimentalStdlibApi",
        "kotlin.time.ExperimentalTime",
        "kotlin.io.encoding.ExperimentalEncodingApi",
        "kotlinx.coroutines.ExperimentalCoroutinesApi",
        "kotlinx.serialization.ExperimentalSerializationApi",
    )
  }

  // explicitApiWarning()
}

tasks {
  compileJava {
    options.apply {
      release = dslJavaVersion.map { it.toInt() }
      isIncremental = true
    }
  }
}

dependencies {
  api(platform(libs.kotlin.bom))
  api(libs.kotlinx.coroutines.core)
  api(libs.kotlinx.datetime)
  api(libs.kotlinx.collections.immutable)
  api(libs.ktor.client.java)
  api(libs.ktor.client.content.negotiation)
  api(libs.ktor.client.encoding)
  api(libs.ktor.client.logging)
  api(libs.ktor.client.resources)
  api(libs.ktor.client.auth)
  api(libs.ktor.serialization.json)
  api(libs.ajalt.mordant.coroutines)
}
