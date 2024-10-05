import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.the

/** Returns the version catalog of this project. */
internal val Project.libs
  get() = the<LibrariesForLibs>()

/**
 * Returns version catalog extension of this project. Give access to all version catalogs available.
 */
internal val Project.catalogs
  get() = the<VersionCatalogsExtension>()

/** Check if the running task is a clean task. */
val Project.hasCleanTask
  get() = gradle.startParameter.taskNames.any { it in listOf("clean", "cleanAll") }

val Project.githubUser
  get() = libs.versions.dev.name.get().lowercase()

val Project.githubRepo
  get() = "https://github.com/${githubUser}/${rootProject.name}"

/** For publishing to maven central and GitHub */
val Project.signingKey
  get() = providers.gradleProperty("signingInMemoryKey")

val Project.signingKeyId
  get() = providers.gradleProperty("signingInMemoryKeyId")

val Project.signingPassword
  get() = providers.gradleProperty("signingInMemoryKeyPassword")

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
