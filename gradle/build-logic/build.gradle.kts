@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.dsl.*

plugins {
  idea
  `kotlin-dsl`
  embeddedKotlin("plugin.serialization")
  alias(libs.plugins.benmanes)
  alias(libs.plugins.spotless)
}

// Java version used for Kotlin Gradle precompiled script plugins.
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
        "kotlinx.coroutines.ExperimentalCoroutinesApi",
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
  // Restrict the java release version used in Gradle kotlin DSL to avoid
  // accidentally using higher version JDK API in build scripts.
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

gradlePlugin {
  plugins {
    // Re-exposure of plugin from dependency. Gradle doesn't expose the plugin itself.
    create("com.gradle.develocity") {
      id = "com.gradle.develocity"
      implementationClass = "com.gradle.develocity.agent.gradle.DevelocityPlugin"
      displayName = "Develocity Gradle Plugin"
      description = "Develocity gradle settings plugin re-exposed from dependency"
    }
  }
}

dependencies {
  // Hack to access version catalog from pre-compiled script plugins.
  // https://github.com/gradle/gradle/issues/15383#issuecomment-779893192
  implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.kotlinx.datetime)
  implementation(libs.ajalt.mordant.coroutines)

  // External plugins deps to use in precompiled script plugins
  // https://docs.gradle.org/current/userguide/implementing_gradle_plugins_precompiled.html#sec:applying_external_plugins
  implementation(libs.build.dokka.plugin)
  implementation(libs.build.dokka.base)
  implementation(libs.build.gradle.develocity)
  implementation(libs.build.nmcp.plugin)
  implementation(libs.build.nexus.plugin)
  implementation(libs.build.spotless.plugin)
  implementation(libs.build.shadow.plugin)
  implementation(libs.build.semver.plugin)
  implementation(libs.build.benmanesversions)
  implementation(libs.build.dependencyanalysis)
  implementation(libs.build.tasktree)
  implementation(libs.build.foojay.resolver)
  implementation(libs.build.jte.plugin)
  implementation(libs.build.tomlj)

  // For using kotlin-dsl in pre-compiled script plugins
  // implementation("${libs.build.kotlin.dsl.get().module}:${expectedKotlinDslPluginsVersion}")
  // testImplementation(gradleTestKit())
}
