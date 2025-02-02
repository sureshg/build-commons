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
    isDownloadJavadoc = true
    isDownloadSources = true
  }
}

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
  implementation(libs.build.spotless.plugin)
  implementation(libs.build.shadow.plugin)
  implementation(libs.build.semver.plugin)
  implementation(libs.build.benmanesversions)
  implementation(libs.build.vanniktech.publish)
  implementation(libs.build.nmcp.plugin)
  implementation(libs.build.foojay.resolver)
  implementation(libs.build.jte.plugin)
  implementation(libs.ajalt.mordant.coroutines)
  implementation(libs.build.tomlj)

  // For "Kotlin Gradle plugin" in pre-compiled script plugins
  implementation(embeddedKotlin("gradle-plugin"))
  // For "kotlin-dsl" plugin in pre-compiled script plugins
  implementation("${libs.build.kotlin.dsl.get().module}:${expectedKotlinDslPluginsVersion}")
}
