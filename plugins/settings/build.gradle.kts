@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  id("org.gradle.kotlin.kotlin-dsl")
  embeddedKotlin("plugin.serialization")
  com.github.`ben-manes`.versions
  com.diffplug.spotless
  plugin.publishing
}

description = "Gradle build settings plugins!"

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
  implementation(projects.plugins.shared)
  implementation(libs.build.kotlinx.kover)
  implementation(libs.build.gradle.develocity)
  implementation(libs.build.semver.plugin)
  implementation(libs.build.foojay.resolver)
  implementation(libs.build.tomlj)
  testImplementation(gradleTestKit())
}
