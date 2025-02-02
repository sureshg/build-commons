@file:Suppress("UnstableApiUsage")

import gg.jte.gradle.GenerateJteTask

plugins {
  plugin.kotlin.dsl
  embeddedKotlin("plugin.serialization")
  // embeddedKotlin("jvm")
  gg.jte.gradle
  plugin.publishing
}

description = "Gradle build project plugins!"

kotlin {
  compilerOptions.optIn.addAll(
      "kotlinx.validation.ExperimentalBCVApi",
      "org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi",
      "org.jetbrains.kotlin.gradle.ExperimentalWasmDsl",
      "org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalDistributionDsl",
      "org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalDceDsl")
}

jte {
  contentType = gg.jte.ContentType.Plain
  generate()
  jteExtension("gg.jte.models.generator.ModelExtension") {
    property("language", "Kotlin")
    // property("interfaceAnnotation", "@foo.bar.MyAnnotation")
    // property("implementationAnnotation", "@foo.bar.MyAnnotation")
  }

  // sourceDirectory = sourceSets.main.map { it.resources.srcDirs.first().toPath() }
  // jteExtension("gg.jte.nativeimage.NativeResourcesExtension")
  // binaryStaticContent = true
  // kotlinCompileArgs = arrayOf("-jvm-target", dslJavaVersion.get())
}

gradlePlugin {
  plugins {
    // Uncomment the id to change plugin id for this pre-compiled plugin
    named("dev.suresh.plugin.common") {
      // id = "${project.group}.${project.name}.common"
      displayName = "Common build-logic plugin"
      description = "Common pre-compiled script plugin"
      tags = listOf("Common Plugin", "build-logic")
    }

    // Dependency Reports plugin
    register("Dependency Reports") {
      id = "dev.suresh.plugin.depreports"
      implementationClass = "plugins.DepReportsPlugin"
      displayName = "Dependency Reports plugin"
      description = "A plugin to list all the resolved artifacts"
      tags = listOf("Dependency Reports", "build-logic")
    }

    // A generic plugin for both project and settings
    register("Generic Plugin") {
      id = "dev.suresh.plugin.generic"
      implementationClass = "plugins.GenericPlugin"
      displayName = "Generic plugin"
      description = "A plugin-aware pre-compiled generic plugin"
      tags = listOf("Generic Plugin", "build-logic")
    }

    // val settingsPlugin by registering {}
  }
}

tasks {
  withType<GenerateJteTask>().configureEach { mustRunAfter("sourcesJar") }

  // Include the generated catalog accessors to the final jar
  // named<Jar>("jar") {
  //    from(sourceSets.main.get().output)
  //    from(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
  //    archiveClassifier = ""
  // }
}

dependencies {
  implementation(projects.plugins.shared)
  implementation(libs.build.zip.prefixer)
  implementation(libs.jte.runtime)
  jteGenerate(libs.jte.models)
  // compileOnly(libs.jte.kotlin)

  // External plugins deps to use in precompiled script plugins
  implementation(libs.build.kotlin)
  // OR implementation(kotlin("gradle-plugin"))
  implementation(libs.build.kotlin.ksp)
  implementation(libs.build.kotlinx.atomicfu)
  implementation(libs.build.kotlin.allopen)
  implementation(libs.build.kotlin.powerassert)
  implementation(libs.build.kotlin.jsplainobjects)
  implementation(libs.build.kotlinx.serialization)
  implementation(libs.build.kotlinx.kover)
  implementation(libs.build.kotlinx.benchmark)
  implementation(libs.build.kotlinx.bcv)
  implementation(libs.build.kmpmt)
  implementation(libs.build.dokka.plugin)
  implementation(libs.build.redacted.plugin)
  implementation(libs.build.nmcp.plugin)
  implementation(libs.build.nexus.plugin)
  implementation(libs.build.spotless.plugin)
  implementation(libs.build.shadow.plugin)
  implementation(libs.build.mrjar.plugin)
  implementation(libs.build.semver.plugin)
  implementation(libs.build.benmanesversions)
  implementation(libs.build.tasktree)
  implementation(libs.build.nativeimage.plugin)
  implementation(libs.build.mokkery.plugin)
  implementation(libs.build.jte.plugin)
  implementation(libs.build.jib.plugin)
  implementation(libs.build.jib.nativeimage.extn)
  implementation(libs.build.github.changelog)
  implementation(libs.build.modulegraph.plugin)
  implementation(libs.build.kopy.plugin)

  // https://github.com/gradle/gradle/issues/15383#issuecomment-779893192
  implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))

  // implementation(libs.build.kotlin.compose.compiler)
  // implementation(libs.build.karakum.plugin)
  // implementation(libs.jte.native)
  // implementation(libs.build.kmp.hierarchy)
  // implementation(libs.build.includegit.plugin)
  // implementation(libs.build.dependencyanalysis)

  // For using kotlin-dsl in pre-compiled script plugins
  // implementation("${libs.build.kotlin.dsl.get().module}:${expectedKotlinDslPluginsVersion}")
}
