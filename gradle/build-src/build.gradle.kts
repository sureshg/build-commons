@file:Suppress("UnstableApiUsage")

import org.gradle.kotlin.dsl.support.expectedKotlinDslPluginsVersion

plugins {
  `kotlin-dsl`
  embeddedKotlin("plugin.serialization")
}

dependencies {
  implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
  implementation(libs.build.spotless.plugin)
  implementation(libs.build.shadow.plugin)
  implementation(libs.build.semver.plugin)
  implementation(libs.build.benmanesversions)
  implementation(libs.build.vanniktech.publish)

  // For using `kotlin-dsl` in pre-compiled script plugins
  implementation("${libs.build.kotlin.dsl.get().module}:${expectedKotlinDslPluginsVersion}")
}
