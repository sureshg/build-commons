@file:Suppress("UnstableApiUsage")

import common.*
import org.jetbrains.kotlin.gradle.dsl.*

plugins {
  id("org.gradle.kotlin.kotlin-dsl")
  com.github.`ben-manes`.versions
  com.diffplug.spotless
}

description = "Kotlin DSL build plugin!"

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
  // Restrict the java release version used in Gradle kotlin DSL to
  // avoid accidentally using higher version JDK API in build scripts.
  compileJava {
    options.apply {
      release = dslJavaVersion.map { it.toInt() }
      isIncremental = true
    }
  }

  validatePlugins {
    failOnWarning = true
    enableStricterValidation = true
  }
}

dependencies {
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.kotlinx.datetime)
  implementation(libs.kotlinx.serialization.json)
  implementation(libs.ajalt.mordant.coroutines)
  testImplementation(gradleTestKit())
  // implementation(libs.ktor.client.java)
  // implementation(libs.ktor.client.content.negotiation)
  // implementation(libs.ktor.client.encoding)
  // implementation(libs.ktor.client.logging)
  // implementation(libs.ktor.client.resources)
  // implementation(libs.ktor.client.auth)
  // implementation(libs.ktor.serialization.json)
}
