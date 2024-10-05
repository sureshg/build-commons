import common.*
import org.gradle.kotlin.dsl.*
import tasks.*

plugins {
  dev.suresh.plugin.root
  dev.suresh.plugin.kotlin.jvm
  com.gradleup.shadow
}

description = "Sandbox App"

application { mainClass = "MainKt" }
