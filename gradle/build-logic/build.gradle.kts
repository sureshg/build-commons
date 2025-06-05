@file:Suppress("UnstableApiUsage")

import org.gradle.kotlin.dsl.support.expectedKotlinDslPluginsVersion
import org.jetbrains.kotlin.gradle.dsl.*

plugins {
  idea
  `kotlin-dsl`
  embeddedKotlin("plugin.serialization")
  alias(libs.plugins.benmanes)
  alias(libs.plugins.spotless)
}

val dslJavaVersion = libs.versions.kotlin.dsl.jvmtarget

idea {
  module {
    isDownloadJavadoc = false
    isDownloadSources = false
  }
}

kotlin {
  compilerOptions {
    jvmTarget = dslJavaVersion.map(JvmTarget::fromTarget)
    freeCompilerArgs.addAll(
        "-Xjdk-release=${dslJavaVersion.get()}",
        "-Xcontext-parameters",
        "-Xno-param-assertions",
        "-Xno-call-assertions",
        "-Xno-receiver-assertions")
    optIn.addAll(
        "kotlin.ExperimentalStdlibApi",
        "kotlin.time.ExperimentalTime",
        "kotlin.io.encoding.ExperimentalEncodingApi",
    )
  }
}

spotless {
  val ktfmtVersion = libs.versions.ktfmt.get()
  kotlin {
    target("src/**/*.kts", "src/**/*.kt")
    ktfmt(ktfmtVersion)
    trimTrailingWhitespace()
    endWithNewline()
  }

  kotlinGradle {
    target("*.kts")
    ktfmt(ktfmtVersion)
    trimTrailingWhitespace()
    endWithNewline()
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

  dependencyUpdates { checkConstraints = true }

  register("cleanAll") {
    description = "Cleans all projects"
    group = LifecycleBasePlugin.CLEAN_TASK_NAME
    allprojects.mapNotNull { it.tasks.findByName("clean") }.forEach { dependsOn(it) }
    // doLast { delete(layout.buildDirectory) }
  }
}

dependencies {
  // Hack to access the version catalog from pre-compiled script plugins.
  // https://github.com/gradle/gradle/issues/15383#issuecomment-779893192
  implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
  implementation(libs.plugins.spotless.dep)
  implementation(libs.plugins.shadow.dep)
  implementation(libs.plugins.semver.dep)
  implementation(libs.plugins.benmanes.dep)
  implementation(libs.plugins.vanniktech.publish.dep)
  implementation(libs.plugins.foojay.resolver.dep)
  implementation(libs.plugins.jte.dep)
  implementation(libs.ajalt.mordant.coroutines)
  implementation(libs.tomlj)
  implementation(libs.bytesize)

  // For 'Kotlin Gradle plugin' in pre-compiled script plugins
  implementation(embeddedKotlin("gradle-plugin"))
  // For 'kotlin-dsl' plugin in pre-compiled script plugins
  implementation(libs.plugins.kotlin.dsl.dep) {
    version { strictly(expectedKotlinDslPluginsVersion) }
  }
}

/**
 * Converts a Plugin Dependency to Gradle's standard dependency notation format. This is used to
 * declare plugin dependencies in the buildscript classpath.
 */
val Provider<PluginDependency>.dep: Provider<String>
  get() = map { "${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version}" }
