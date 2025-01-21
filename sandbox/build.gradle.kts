import common.*
import org.gradle.kotlin.dsl.*
import tasks.*

plugins {
  id("dev.suresh.plugin.root") version "+"
  id("dev.suresh.plugin.kotlin.mpp") version "+"
  alias(libs.plugins.shadow)
}

kotlin { jvmTarget(project) }

description = "Sandbox App"

application { mainClass = "MainKt" }
