import common.mavenCentralPassword
import common.mavenCentralUsername

plugins {
  `kotlin-dsl`
  plugins.root
  com.gradleup.nmcp
}

description = "Common gradle build logic for all projects"

nmcp {
  publishAggregation {
    project(":plugins")
    project(":catalog")

    username = mavenCentralUsername
    password = mavenCentralPassword
    publicationType = "MANUAL"
  }
}
