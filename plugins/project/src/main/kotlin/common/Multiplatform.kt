package common

import org.gradle.api.Project
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsBinaryMode
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsTargetDsl

fun KotlinMultiplatformExtension.jvmTarget(project: Project) =
    with(project) {
      jvm {
        compilations.configureEach {
          compileJavaTaskProvider?.configure { configureJavac(project) }
        }
        compilerOptions { configureKotlinJvm(project) }

        mainRun {
          mainClass = libs.versions.app.mainclass
          setArgs(runJvmArgs)
        }

        // val test by testRuns.existing
        testRuns.configureEach { executionTask.configure { configureJavaTest() } }

        // Configures JavaExec task with name "runJvm" and Gradle distribution "jvmDistZip"
        if (extraProp("enableKmpExec", false)) {
          binaries {
            executable {
              mainClass = libs.versions.app.mainclass
              applicationDefaultJvmArgs = runJvmArgs
              applicationDistribution.duplicatesStrategy = DuplicatesStrategy.INCLUDE
            }
          }
        }

        // attributes.attribute(KotlinPlatformType.attribute, KotlinPlatformType.jvm)
      }

      sourceSets {
        jvmMain {
          // dependsOn(jvmCommon)
          dependencies {
            // api(libs.kotlin.stdlib)
            api(libs.kotlin.metadata.jvm)
            api(libs.ktor.client.java)
            api(libs.slf4j.api)
            api(libs.slf4j.jul)
            api(libs.kotlinx.coroutines.slf4j)
            api(libs.jspecify)
            api(libs.bundles.keystore)
            api(libs.google.auto.annotations)
            ksp(libs.ksp.auto.service)
          }
        }

        jvmTest {
          dependencies {
            api(project.dependencies.platform(libs.junit.bom))
            api(project.dependencies.platform(libs.testcontainers.bom))
            api(libs.mockk)
            api(libs.mockk.bdd)
            api(libs.slf4j.simple)
            api(libs.testcontainers.junit5)
            api(libs.testcontainers.postgresql)
            // api(kotlin("reflect"))
            // api(libs.konsist)
          }
        }
      }
    }

fun KotlinJsTargetDsl.webConfig(project: Project) =
    with(project) {
      browser {
        commonWebpackConfig {
          cssSupport { enabled = true }
          // outputFileName = "app.js"
          // scssSupport { enabled = true }
          // sourceMaps = true
        }

        runTask { sourceMaps = false }
        testTask {
          enabled = true
          testLogging { configureLogEvents() }
          useKarma { useChromeHeadless() }
        }

        // distribution { outputDirectory = file("$projectDir/docs") }
      }

      // useEsModules()
      if (isSharedProject.not()) {
        binaries.executable()
      }
      generateTypeScriptDefinitions()
      compilerOptions { configureKotlinJs() }
      testRuns.configureEach { executionTask.configure {} }
    }

fun KotlinMultiplatformExtension.webDeps(project: Project) =
    with(project) {
      sourceSets {
        webMain {
          dependencies {
            api(libs.ktor.client.js)
            api(libs.kotlinx.browser)
            // api(npm("@js-joda/timezone", libs.versions.npm.jsjoda.tz.get()))
          }
          // kotlin.srcDir("src/main/kotlin")
          // resources.srcDir("src/main/resources")
        }
      }
    }

fun KotlinMultiplatformExtension.jsTarget(project: Project) {
  js { webConfig(project) }
  webDeps(project)
}

fun KotlinMultiplatformExtension.wasmJsTarget(project: Project) {
  wasmJs { webConfig(project) }
  webDeps(project)
}

fun KotlinMultiplatformExtension.wasmWasiTarget(project: Project) =
    with(project) {
      wasmWasi {
        nodejs()
        if (isSharedProject.not()) {
          binaries
              .executable()
              .filter { it.mode == KotlinJsBinaryMode.PRODUCTION }
              .forEach { binary ->
                val wasmFilename = binary.mainFileName.map { it.replaceAfterLast(".", "wasm") }
                val wasmFile =
                    binary.linkTask.flatMap { it.destinationDirectory.file(wasmFilename) }
                // Add generated WASM binary as maven publication
                mavenPublication { artifact(wasmFile) { classifier = targetName } }
              }
        }

        compilations.all {
          compileTaskProvider.configure {
            compilerOptions.freeCompilerArgs.addAll(
                listOf("-Xwasm-use-traps-instead-of-exceptions")
            )
          }
        }
      }

      sourceSets {
        wasmWasiMain { dependencies {} }
        wasmWasiTest { kotlin {} }
      }
    }

fun KotlinMultiplatformExtension.nativeTargets(
    project: Project,
    configure: KotlinNativeTarget.() -> Unit = {},
) =
    with(project) {
      fun KotlinNativeTarget.configureAll() {
        compilerOptions {
          // freeCompilerArgs.addAll()
        }
        configure()
      }

      compilerOptions {
        optIn.addAll(
            "kotlinx.cinterop.ExperimentalForeignApi",
            "kotlin.experimental.ExperimentalNativeApi",
        )
      }

      macosX64 { configureAll() }
      macosArm64 { configureAll() }
      linuxX64 { configureAll() }
      linuxArm64 { configureAll() }
      if (isWinTargetEnabled) {
        mingwX64 { configureAll() }
      }

      sourceSets { nativeMain { dependencies { api(libs.ktor.client.curl) } } }
    }

fun KotlinMultiplatformExtension.addKspDependencyForAllTargets(
    project: Project,
    dependencyNotation: Any,
    configurationNameSuffix: String = "",
) =
    with(project) {
      // val kotlin = extensions.getByType<KotlinMultiplatformExtension>()
      targets
          .filter { target ->
            // Don't add KSP for common target, only final platforms
            target.platformType != KotlinPlatformType.common
          }
          .forEach { target ->
            dependencies.add(
                "ksp${target.targetName.replaceFirstChar { it.uppercaseChar() }}$configurationNameSuffix",
                dependencyNotation,
            )
          }
    }
