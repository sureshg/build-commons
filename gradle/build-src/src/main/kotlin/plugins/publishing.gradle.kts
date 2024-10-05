package plugins

import com.vanniktech.maven.publish.SonatypeHost
import java.time.Year
import kotlin.text.lowercase
import libs

plugins {
  id("com.javiersc.semver")
  // https://github.com/vanniktech/gradle-maven-publish-plugin/issues/846
  // id("org.gradle.kotlin.kotlin-dsl")
  id("com.vanniktech.maven.publish")
}

group = libs.versions.group.get()

mavenPublishing {
  publishToMavenCentral(host = SonatypeHost.CENTRAL_PORTAL, automaticRelease = true)

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
