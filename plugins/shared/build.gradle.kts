@file:Suppress("UnstableApiUsage")

plugins {
  plugin.kotlin.dsl
  embeddedKotlin("plugin.serialization")
  plugin.publishing
}

description = "Shared module for build plugins!"
