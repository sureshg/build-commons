import common.*
import org.gradle.kotlin.dsl.*
import tasks.*

plugins {
  dev.suresh.plugin.root
  dev.suresh.plugin.kotlin.mpp
  com.gradleup.shadow
}

kotlin { jvmTarget(project) }

description = "Sandbox App"

application { mainClass = "MainKt" }
