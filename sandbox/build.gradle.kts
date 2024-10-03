import common.*

plugins {
  plugins.root
  plugins.kotlin.jvm
  id("com.gradleup.shadow")
}

application { mainClass = "MainKt" }
