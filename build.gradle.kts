plugins {
  plugins.root
  com.gradleup.nmcp
}

description = "Common gradle build logic for all projects"

nmcp {
  publishAggregation {
    project(":plugins")
    project(":catalog")
  }
}
