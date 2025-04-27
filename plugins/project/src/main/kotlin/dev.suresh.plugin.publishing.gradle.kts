import com.google.cloud.tools.jib.gradle.JibExtension
import com.vanniktech.maven.publish.SonatypeHost
import common.*
import java.time.Year
import org.jetbrains.kotlin.gradle.tasks.KotlinNativeLink

plugins { com.vanniktech.maven.publish }

group = libs.versions.group.get()

publishing {
  repositories {
    maven {
      name = "local"
      url = uri(layout.buildDirectory.dir("repo"))
    }

    maven {
      name = "GitHubPackages"
      url = uri(githubPackage(libs.versions.dev.name.get(), rootProject.name))
      credentials {
        username = githubPackagesUsername.orNull
        password = githubPackagesPassword.orNull
      }
    }
  }

  publications {
    // Kotlin Multiplatform
    pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
      // val customFiles = configurations.findByName("customFiles")?.artifacts.orEmpty()
      // withType<MavenPublication>().configureEach { customFiles.forEach { artifact(it) } }
    }

    // Kotlin JVM - components["java"]
    pluginManager.withPlugin("java") {}

    // Java Platform (BOM) - components["javaPlatform"]
    pluginManager.withPlugin("java-platform") {}

    // Gradle version catalog - components["versionCatalog"]
    pluginManager.withPlugin("version-catalog") {}

    // Configures GHCR credentials for Jib
    pluginManager.withPlugin("com.google.cloud.tools.jib") {
      configure<JibExtension> {
        to {
          if (image.orEmpty().startsWith("ghcr.io", ignoreCase = true)) {
            auth {
              username = githubPackagesUsername.orNull
              password = githubPackagesPassword.orNull
            }
          }
        }
      }
    }
  }
}

mavenPublishing {
  publishToMavenCentral(host = SonatypeHost.CENTRAL_PORTAL, automaticRelease = true)

  if (hasSigningKey) {
    signAllPublications()
  }

  pom {
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

tasks {
  // Suppressing publication validation errors
  withType<GenerateModuleMetadata> { suppressedValidationErrors.add("enforced-platform") }

  withType<AbstractPublishToMaven>().configureEach { mustRunAfter(withType<Sign>()) }

  // For publishing kotlin native binaries
  withType<AbstractPublishToMaven>().configureEach { mustRunAfter(withType<KotlinNativeLink>()) }
}
