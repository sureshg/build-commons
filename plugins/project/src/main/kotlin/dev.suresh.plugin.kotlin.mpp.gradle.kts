@file:Suppress("UnstableApiUsage")

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.google.cloud.tools.jib.gradle.JibTask
import common.*
import org.gradle.internal.os.OperatingSystem
import org.jetbrains.kotlin.gradle.targets.js.nodejs.*
import org.jetbrains.kotlin.gradle.targets.js.npm.*
import org.jetbrains.kotlin.gradle.targets.js.npm.tasks.*
import org.jetbrains.kotlin.gradle.targets.wasm.nodejs.*
import org.jetbrains.kotlin.gradle.targets.wasm.npm.*
import tasks.*

plugins {
  kotlin("multiplatform")
  kotlin("plugin.serialization")
  kotlin("plugin.power-assert")
  kotlin("plugin.js-plain-objects")
  id("dev.suresh.plugin.common")
  id("dev.suresh.plugin.kotlin.docs")
  com.google.devtools.ksp
  dev.zacsweers.redacted
  // com.javiersc.kotlin.kopy
  // kotlin("plugin.compose")
  // org.gradle.kotlin.`kotlin-dsl`
}

configurations.configureEach { resolutionStrategy { failOnNonReproducibleResolution() } }

kotlin {
  jvmToolchain { configureJvmToolchain(project) }
  compilerOptions { configureKotlinCommon(project) }
  applyDefaultHierarchyTemplate()

  @OptIn(org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation::class)
  abiValidation {
    enabled = false
    filters { excluded { byNames.addAll("BuildConfig", $$"BuildConfig$Host") } }
    klib { keepUnsupportedTargets = false }
  }

  dependencies {
    api(platform(libs.kotlin.bom))
    api(platform(libs.kotlinx.coroutines.bom))
    api(platform(libs.kotlinx.serialization.bom))
    api(platform(libs.ktor.bom))
    api(platform(libs.kotlin.wrappers.bom))

    api(libs.kotlinx.coroutines.core)
    api(libs.kotlinx.datetime)
    api(libs.kotlinx.io.core)
    api(libs.kotlinx.serialization.json)
    api(libs.kotlinx.serialization.json.io)
    api(libs.kotlinx.collections.immutable)
    api(libs.kotlin.redacted.annotations)
    api(libs.kotlin.retry)
    api(libs.kotlin.logging)
    api(libs.ktor.client.core)
    api(libs.ktor.client.cio)
    api(libs.ktor.client.content.negotiation)
    api(libs.ktor.client.encoding)
    api(libs.ktor.client.logging)
    api(libs.ktor.client.resources)
    api(libs.ktor.client.auth)
    api(libs.ktor.client.serialization)
    api(libs.ktor.client.websockets)
    api(libs.ktor.serialization.json)
    // api(libs.kotlinx.html)

    testImplementation(kotlin("test"))
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.ktor.client.mock)
  }

  // applyDefaultHierarchyTemplate {
  //   common {
  //     group("posix") {
  //       // Using group will add the intermediate source sets
  //       group("linux")
  //       group("apple")
  //     }
  //
  //     group("jsAndWasmShared") {
  //       withJs()
  //       withWasmJs()
  //       withWasmWasi()
  //     }
  //   }
  // }

  // ---- Configure specific targets/compilations/source-sets ----
  // targets.withType<KotlinJvmTarget>().configureEach { compilerOptions {} }
  // targets.matching { it.platformType == js }.configureEach { apply(plugin = ...) }

  // Get target compilations
  // val commonTarget = targets.first { it.platformType == KotlinPlatformType.common }
  // OR targets["metadata"]
  // val compilation = commonTarget.compilations["main"]

  // Add a task output as sourceSet
  // compilation.defaultSourceSet.kotlin.srcDir(buildConfig)

  // Add new sourceSet
  // val newSourceSet = sourceSets.create("gen")
  // compilation.defaultSourceSet.dependsOn(newSourceSet)
  // ---- Done ----

  // kotlinDaemonJvmArgs = defaultJvmArgs
  // explicitApiWarning()
}

ksp {
  allWarningsAsErrors = false
  // excludedSources.from(generateCodeTask)
}

powerAssert {
  functions =
      listOf(
          "kotlin.assert",
          "kotlin.test.assertTrue",
          "kotlin.test.assertEquals",
          "kotlin.test.assertNull",
          "kotlin.require",
      )
}

redacted {
  enabled = true
  replacementString = "█"
}

tasks {
  val buildConfigExtn = extensions.create<BuildConfigExtension>("buildConfig")
  val buildConfig = register<BuildConfig>("buildConfig", buildConfigExtn)
  buildConfig.configure { enabled = buildConfigExtn.enabled.get() }

  kotlin.sourceSets.commonMain { kotlin.srcDirs(buildConfig) }
  // withType<KspAATask>().configureEach { dependsOn(buildConfig) }
  // compileKotlinMetadata { dependsOn(buildConfig) }

  withType<KotlinNpmInstallTask>().configureEach { configureKotlinNpm() }

  // withType<Kotlin2JsCompile>().configureEach {}

  withType<Jar>().configureEach {
    manifest { attributes(defaultJarManifest) }
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
  }

  withType<ProcessResources>().configureEach {
    val version = project.version.toString()
    val rootProjectName = rootProject.name
    val moduleName = project.name

    inputs.property("version", version)
    filesMatching("**/*-res.txt") {
      expand(
          "name" to rootProjectName,
          "module" to moduleName,
          "version" to version,
      )
    }
    filesMatching("**/*.yaml") {
      filter { line ->
        line.replace("{project.name}", rootProjectName).replace("{project.version}", version)
      }
    }
  }

  pluginManager.withPlugin("com.gradleup.shadow") {
    val buildExecutable by
        registering(ReallyExecJar::class) {
          // https://gradleup.com/shadow/kotlin-plugins/
          jarFile = named<ShadowJar>("shadowJar").flatMap { it.archiveFile }
          javaOpts = runJvmArgs
          execJarFile = layout.buildDirectory.dir("libs").map { it.file(project.name) }
          onlyIf { OperatingSystem.current().isUnix }
        }

    build { finalizedBy(buildExecutable) }

    // Shows how to register a shadowJar task for the default jvm target
    register<ShadowJar>("shadowJvmJar") {
      val main by kotlin.jvm().compilations
      // allOutputs == classes + resources
      from(main.output.allOutputs)
      val runtimeDepConfig =
          project.configurations.getByName(main.runtimeDependencyConfigurationName)
      configurations = listOf(runtimeDepConfig)
      archiveClassifier = "jvm-all"
      mergeServiceFiles()
      duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }
  }

  pluginManager.withPlugin("com.google.cloud.tools.jib") {
    withType<JibTask>().configureEach { notCompatibleWithConfigurationCache("because Jib#3132") }
  }
}

// var npmEnabled: String? by rootProject.extra

plugins.withType<NodeJsPlugin> {
  the<NodeJsEnvSpec>().apply {
    download = true
    // version = libs.versions.nodejs.version.get()
    // downloadBaseUrl = "https://nodejs.org/download/nightly"
  }

  rootProject.the<NpmExtension>().apply {
    lockFileDirectory = project.rootDir.resolve("gradle/kotlin-js-store")
  }
}

plugins.withType<WasmNodeJsPlugin> {
  the<WasmNodeJsEnvSpec>().apply {
    download = true
    // version = libs.versions.nodejs.version.get()
    // downloadBaseUrl = "https://nodejs.org/download/nightly"
  }
  rootProject.the<WasmNpmExtension>().apply {
    lockFileDirectory = project.rootDir.resolve("gradle/kotlin-js-store/wasm")
  }
}

// Expose shared js/wasm resource as configuration to be consumed by other projects.
// https://docs.gradle.org/current/userguide/cross_project_publications.html#sec:simple-sharing-artifacts-between-projects
artifacts {
  if (isSharedProject) {
    tasks.findByName("jsProcessResources")?.let {
      val sharedJsResources by configurations.consumable("sharedJsResources")
      add(sharedJsResources.name, provider { it })
    }

    tasks.findByName("wasmJsProcessResources")?.let {
      val sharedWasmResources by configurations.consumable("sharedWasmResources")
      add(sharedWasmResources.name, provider { it })
    }
  }
}

dependencies {
  // add("kspJvm", project(":ksp-processor"))
}
