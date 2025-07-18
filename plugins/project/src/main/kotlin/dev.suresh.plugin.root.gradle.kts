import com.github.ajalt.mordant.rendering.TextColors.*
import common.*
import common.Platform
import org.gradle.api.publish.plugins.PublishingPlugin.*
import org.gradle.kotlin.dsl.*

plugins {
  idea
  wrapper
  id("dev.suresh.plugin.kotlin.docs")
  id("dev.suresh.plugin.publishing")
  com.github.`ben-manes`.versions
  com.dorongold.`task-tree`
  // id("gg.jte.gradle")
  // com.autonomousapps.`dependency-analysis`
}

if (hasCleanTask) {
  logger.warn(
      yellow(
              """
            | CLEANING ALMOST NEVER FIXES YOUR BUILD!
            | Cleaning is often a last-ditch effort to fix perceived build problems that aren't going to
            | actually be fixed by cleaning. What cleaning will do though is make your next few builds
            | significantly slower because all the incremental compilation data has to be regenerated,
            | so you're really just making your day worse.
            """)
          .trimMargin(),
  )
}

gradle.projectsEvaluated { logger.lifecycle(magenta("=== Projects Configuration Completed ===")) }

idea {
  module {
    isDownloadJavadoc = false
    isDownloadSources = false
  }
  project.vcs = "Git"
}

// jte {
//   contentType = ContentType.Plain
//   generateNativeImageResources = true
//   generate()
// }

// Skip test tasks on skip.test=true
if (skipTest) {
  allprojects { tasks.matching { it is AbstractTestTask }.configureEach { onlyIf { false } } }
}

tasks {
  // Dependency version updates
  dependencyUpdates {
    checkConstraints = true
    gradle.includedBuilds.forEach { incBuild ->
      incBuild.projectDir
          .resolve("build.gradle.kts")
          .takeIf { it.exists() }
          ?.let { dependsOn(incBuild.task(":dependencyUpdates")) }
    }
  }

  spotlessApply {
    gradle.includedBuilds.forEach { incBuild ->
      incBuild.projectDir
          .resolve("build.gradle.kts")
          .takeIf { it.exists() }
          ?.let { dependsOn(incBuild.task(":spotlessApply")) }
    }
  }

  // Reproducible builds
  withType<AbstractArchiveTask>().configureEach {
    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true
    dirPermissions { unix("rwxrwxr-x") }
    filePermissions { unix("rw-rw-r--") }
  }

  val githubActionOutput by registering {
    description = "Set Github workflow action output for this build"
    group = BasePlugin.BUILD_GROUP

    val projectName = project.name
    val projectGroup = project.group
    val projectVersion = project.version

    doLast {
      with(GithubAction) {
        setOutput("name", projectName)
        setOutput("group", projectGroup)
        setOutput("version", projectVersion)
        setOutput("artifact_name", "$projectName-$projectVersion")
      }
    }
  }

  // Set GitHub workflow action output for this build
  build { finalizedBy(githubActionOutput) }

  register("buildAndPublish") {
    description = "Build and publish all artifacts"
    group = BasePlugin.BUILD_GROUP

    dependsOn(allprojects.map { it.tasks.build })
    dependsOn(":allTestReports", ":dokkaGenerate", ":koverHtmlReport")

    when {
      // Publishing to all repos on GitHub Action tag build
      GithubAction.isTagBuild && Platform.isLinux -> {
        logger.lifecycle(magenta("Publishing to all repositories is enabled!"))
        allprojects
            .mapNotNull { it.tasks.findByName(PUBLISH_LIFECYCLE_TASK_NAME) }
            .forEach { dependsOn(it) }
      }

      // Publish is disabled on GitHub Action non-tag builds
      GithubAction.isEnabled -> logger.lifecycle(red("Publishing is disabled!"))

      // Publishing to local repo on other platforms
      else -> {
        logger.lifecycle(yellow("Publishing to local repo is enabled!"))
        allprojects
            .mapNotNull { it.tasks.findByName("publishAllPublicationsToLocalRepository") }
            .forEach { dependsOn(it) }
      }
    }
  }

  // Auto-format all source files
  pluginManager.withPlugin("com.diffplug.spotless") {
    // tasks.withType<ProcessResources>().configureEach { dependsOn(":spotlessApply") }
  }

  // Run the checkBestPractices check for build-logic included builds.
  register("checkBuildLogicBestPractices") {
    description = "Run the checkBestPractices check for build-logic included builds!"
    group = BasePlugin.BUILD_GROUP

    dependsOn(
        gradle.includedBuilds
            .filter { it.name == buildLogicProjectName }
            .mapNotNull { it.task(":checkBestPractices") })
  }

  register<Copy>("setUpGitHooks") {
    description = "Set up pre-commit git hooks"
    group = "help"
    from("$rootDir/gradle/.githooks")
    into("$rootDir/.git/hooks")
  }

  // Clean all composite builds
  register("cleanAll") {
    description = "Clean all projects including composite builds"
    group = LifecycleBasePlugin.CLEAN_TASK_NAME

    dependsOn(gradle.includedBuilds.map { it.task(":cleanAll") })
    allprojects.mapNotNull { it.tasks.findByName("clean") }.forEach { dependsOn(it) }
  }

  register("v") {
    description = "Print the ${rootProject.name} version!"
    doLast { println(rootProject.version.toString()) }
  }

  wrapper {
    gradleVersion = libs.versions.gradle.asProvider().get()
    distributionType = Wrapper.DistributionType.BIN
    // distributionUrl = "${Repo.GRADLE_DISTRO}/gradle-$gradleVersion-bin.zip"
  }

  withType<UpdateDaemonJvm> {
    languageVersion = libs.versions.kotlin.dsl.jvmtarget.map { JavaLanguageVersion.of(it) }
  }

  defaultTasks("clean", "tasks", "--all")
}
