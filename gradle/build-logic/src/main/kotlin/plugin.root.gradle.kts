import com.github.ajalt.mordant.rendering.TextColors.*
import common.*

plugins {
  idea
  wrapper
  com.github.`ben-manes`.versions
  com.diffplug.spotless
}

group = libs.versions.group.get()

if (hasCleanTask) {
  logger.warn(
      yellow(
              """
            | CLEANING ALMOST NEVER FIXES YOUR BUILD!
            | Cleaning is often a last-ditch effort to fix perceived build problems that aren't going to
            | actually be fixed by cleaning. What cleaning will do though is make your next few builds
            | significantly slower because all the incremental compilation data has to be regenerated,
            | so you're really just making your day worse.
            """
          )
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

spotless {
  java {
    // googleJavaFormat(libs.versions.google.javaformat.get())
    palantirJavaFormat(libs.versions.palantir.javaformat.get()).formatJavadoc(true)
    target("**/*.java")
    targetExclude("**/build/**")
  }

  // if(plugins.hasPlugin(JavaPlugin::class)){ }

  val ktfmtVersion = libs.versions.ktfmt.get()
  kotlin {
    ktfmt(ktfmtVersion)
    target("**/*.kt")
    targetExclude("**/build/**")
    trimTrailingWhitespace()
    endWithNewline()
    // licenseHeader(rootProject.file("gradle/license-header.txt"))
  }

  kotlinGradle {
    ktfmt(ktfmtVersion)
    target("**/*.gradle.kts")
    targetExclude("**/build/**")
    trimTrailingWhitespace()
    endWithNewline()
  }

  format("misc") {
    target("**/*.md", "**/.gitignore", "**/.kte")
    targetExclude("**/build/**")
    trimTrailingWhitespace()
    leadingTabsToSpaces(2)
    endWithNewline()
  }
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

  build {
    gradle.includedBuilds.forEach { incBuild ->
      incBuild.projectDir
          .resolve("build.gradle.kts")
          .takeIf { it.exists() }
          ?.let { dependsOn(incBuild.task(":build")) }
    }
  }

  register("cleanAll") {
    description = "Clean all projects including composite builds"
    group = LifecycleBasePlugin.CLEAN_TASK_NAME

    dependsOn(gradle.includedBuilds.map { it.task(":cleanAll") })
    allprojects.mapNotNull { it.tasks.findByName("clean") }.forEach { dependsOn(it) }
  }

  register("v") {
    val version = rootProject.version.toString()
    description = "Print the ${rootProject.name} version!"
    doLast { println(version) }
  }

  wrapper {
    gradleVersion = libs.versions.gradle.asProvider().get()
    distributionType = Wrapper.DistributionType.BIN
    // distributionUrl = "${Repo.GRADLE_DISTRO}/gradle-$gradleVersion-bin.zip"
  }

  defaultTasks("clean", "tasks", "--all")
}
