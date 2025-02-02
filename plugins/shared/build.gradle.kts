@file:Suppress("UnstableApiUsage")

plugins {
  id("plugin.kotlin-dsl")
  embeddedKotlin("plugin.serialization")
  plugin.publishing
}

description = "Shared module for build plugins!"
