plugins {
  idea
  wrapper
  alias(libs.plugins.semver)
  alias(libs.plugins.benmanes)
  alias(libs.plugins.spotless)

  // Workaround for vanniktech publish plugin
  `kotlin-dsl`
  alias(libs.plugins.vanniktech.publish) apply false
}

gradle.projectsEvaluated { logger.lifecycle("=== Projects Configuration Completed ===") }

idea {
  module {
    isDownloadJavadoc = true
    isDownloadSources = true
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
  // if(plugins.hasPlugin(JavaPlugin::class.java)){ }

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
    indentWithSpaces(2)
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
