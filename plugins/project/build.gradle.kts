@file:Suppress("UnstableApiUsage")

import common.*

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
      "org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi",
      "org.jetbrains.kotlin.gradle.ExperimentalWasmDsl",
      "org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalDistributionDsl",
  )
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

dependencies {
  implementation(project(":plugins:shared"))
  implementation(libs.zip.prefixer)
  implementation(libs.jte.runtime)
  jteGenerate(libs.jte.models)
  // External plugins deps to use in precompiled script plugins
  // implementation(platform(libs.kotlin.bom))
  implementation(libs.plugins.kotlin.multiplatform.dep)
  implementation(libs.plugins.kotlin.allopen.dep)
  implementation(libs.plugins.kotlin.powerassert.dep)
  implementation(libs.plugins.kotlin.js.plainobjects.dep)
  implementation(libs.plugins.kotlin.ksp.dep)
  implementation(libs.plugins.kotlin.dataframe.dep)
  implementation(libs.plugins.kotlinx.atomicfu.dep)
  implementation(libs.plugins.kotlinx.serialization.dep)
  implementation(libs.plugins.kotlinx.kover.dep)
  implementation(libs.plugins.kotlinx.benchmark.dep)
  implementation(libs.plugins.jetbrains.dokka.dep)
  implementation(libs.plugins.graalvm.nativeimage.dep)
  implementation(libs.plugins.redacted.dep)
  implementation(libs.plugins.spotless.dep)
  implementation(libs.plugins.shadow.dep)
  implementation(libs.plugins.semver.dep)
  implementation(libs.plugins.benmanes.dep)
  implementation(libs.plugins.jte.dep)
  implementation(libs.plugins.jib.dep)
  implementation(libs.plugins.vanniktech.publish.dep)
  implementation(libs.jib.nativeimage.extn)
  implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
  // implementation(libs.plugins.kopy.dep)
  // implementation(libs.plugins.exoquery.dep)
  // implementation(libs.kmpmt)
  // compileOnly(libs.jte.kotlin)
}
