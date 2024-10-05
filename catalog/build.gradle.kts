plugins {
  `version-catalog`
  plugin.publishing
}

description = "Gradle version catalog!"

catalog { versionCatalog { from(files(rootDir.resolve("gradle/libs.versions.toml"))) } }
