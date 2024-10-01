import com.vanniktech.maven.publish.SonatypeHost

plugins {
  `version-catalog`
  alias(libs.plugins.semver)
  alias(libs.plugins.vanniktech.publish)
}

group = libs.versions.group.get()

description = "Gradle version catalog!"

catalog { versionCatalog { from(files(rootDir.resolve("gradle/libs.versions.toml"))) } }

mavenPublishing {
  publishToMavenCentral(host = SonatypeHost.CENTRAL_PORTAL, automaticRelease = false)
  signAllPublications()
}
