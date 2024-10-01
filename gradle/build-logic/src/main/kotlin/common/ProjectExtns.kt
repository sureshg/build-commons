@file:Suppress("UnstableApiUsage")

package common

import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.the

/** Returns version catalog of this project. */
internal val Project.libs
  get() = the<LibrariesForLibs>()

val Project.isRootProject
  get() = this == rootProject

val Project.skipTest
  get() = providers.gradleProperty("skip.test").map(String::toBoolean).getOrElse(false)

val Project.hasCleanTask
  get() = gradle.startParameter.taskNames.any { it in listOf("clean", "cleanAll") }

/** Java version properties. */
val Project.javaVersion
  get() = libs.versions.java.asProvider().map { JavaVersion.toVersion(it) }

val Project.javaRelease
  get() = javaVersion.map { it.majorVersion.toInt() }

val Project.toolchainVersion
  get() = javaVersion.map { JavaLanguageVersion.of(it.majorVersion) }

/** Kotlin version properties. */
val Project.kotlinVersion
  get() = libs.versions.kotlin.asProvider()

val Project.kotlinDslJvmTarget
  get() = libs.versions.kotlin.dsl.jvmtarget

val Project.orgName
  get() = libs.versions.org.name.get()

val Project.githubUser
  get() = libs.versions.dev.name.get().lowercase()

val Project.githubRepo
  get() = "https://github.com/${githubUser}/${rootProject.name}"

/** For publishing to maven central and GitHub */
val Project.signingKey
  get() = providers.gradleProperty("signingKey")

val Project.signingKeyId
  get() = providers.gradleProperty("signingKeyId")

val Project.signingPassword
  get() = providers.gradleProperty("signingPassword")

val Project.hasSigningKey
  get() = signingKey.orNull.isNullOrBlank().not() && signingPassword.orNull.isNullOrBlank().not()

val Project.mavenCentralUsername
  get() = providers.gradleProperty("mavenCentralUsername")

val Project.mavenCentralPassword
  get() = providers.gradleProperty("mavenCentralPassword")

val Project.githubActor
  get() = providers.gradleProperty("githubActor")

val Project.githubToken
  get() = providers.gradleProperty("githubToken")

/**
 * Generates the URL for the GitHub package repository based on the owner and repository name.
 *
 * @param owner The owner of the GitHub repository.
 * @param repository The name of the GitHub repository.
 * @return The URL of the GitHub package repository.
 */
fun githubPackage(owner: String, repository: String) =
    "https://maven.pkg.github.com/${owner.lowercase()}/$repository"

/** Converts a string to camelcase by splitting it by space, dash, underscore, or dot. */
val String.camelCase: String
  get() =
      split("""[.\-_ ]""".toRegex())
          .mapIndexed { idx, s -> if (idx == 0) s else s.replaceFirstChar { it.uppercaseChar() } }
          .joinToString("")
