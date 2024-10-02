import com.vanniktech.maven.publish.SonatypeHost
import java.time.Year

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

  pom {
    val githubUser = libs.versions.dev.name.get().lowercase()
    val githubRepo = "https://github.com/${githubUser}/${rootProject.name}"

    name = provider { "${project.group}:${project.name}" }
    description = provider { project.description }
    inceptionYear = Year.now().toString()
    url = githubRepo

    developers {
      developer {
        name = libs.versions.dev.name
        email = libs.versions.dev.email
        organization = libs.versions.org.name
        organizationUrl = libs.versions.org.url
      }
    }

    licenses {
      license {
        name = "The Apache Software License, Version 2.0"
        url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
      }
    }

    scm {
      url = githubRepo
      connection = "scm:git:$githubRepo.git"
      developerConnection = "scm:git:$githubRepo.git"
    }
  }
}
