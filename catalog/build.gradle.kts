plugins {
  `version-catalog`
  plugins.publishing
}

description = "Gradle version catalog!"

catalog { versionCatalog { from(files(rootDir.resolve("gradle/libs.versions.toml"))) } }
