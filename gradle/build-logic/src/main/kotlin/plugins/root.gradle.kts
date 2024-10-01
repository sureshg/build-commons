package plugins

import com.github.ajalt.mordant.rendering.TextColors.*
import common.*
import common.Platform
import org.gradle.api.publish.plugins.PublishingPlugin.PUBLISH_LIFECYCLE_TASK_NAME
import org.gradle.kotlin.dsl.*

plugins {
  idea
  wrapper
  com.github.`ben-manes`.versions
  id("plugins.kotlin.docs")
  id("plugins.publishing")
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
    isDownloadJavadoc = true
    isDownloadSources = true
  }
  project.vcs = "Git"
}

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
  }

  register("buildAndPublish") {
    description = "Build and publish all artifacts"
    group = BasePlugin.BUILD_GROUP

    dependsOn(allprojects.map { it.tasks.build })
    dependsOn(":dokkaHtmlMultiModule")

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
    distributionType = Wrapper.DistributionType.ALL
    // distributionUrl = "${Repo.GRADLE_DISTRO}/gradle-$gradleVersion-bin.zip"
  }

  defaultTasks("clean", "tasks", "--all")
}
