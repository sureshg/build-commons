import common.*
import org.gradle.kotlin.dsl.*
import tasks.*

val enableKmpExec by extra(true)

plugins {
  id("dev.suresh.plugin.root") version "+"
  id("dev.suresh.plugin.kotlin.mpp") version "+"
  alias(libs.plugins.shadow)
}

description = "Sandbox App"

buildConfig {
  enabled = true
  projectName = rootProject.name
  projectVersion = project.version.toString()
  projectDesc = rootProject.description
  gitCommit = semver.commits.get().first()
  catalogVersions = project.versionCatalogMapOf()
}

kotlin { jvmTarget(project) }
