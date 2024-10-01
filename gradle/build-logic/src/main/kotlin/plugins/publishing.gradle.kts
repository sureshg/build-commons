package plugins

import common.*
import nmcp.NmcpPublishTask

plugins {
  `maven-publish`
  signing
  com.gradleup.nmcp
}

// Nexus plugin needs to apply to the root project only
if (isRootProject) {
  apply(plugin = "io.github.gradle-nexus.publish-plugin")
}

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
        // findProperty("githubActor")
        username = githubActor.orNull ?: System.getenv("GITHUB_ACTOR")
        password = githubToken.orNull ?: System.getenv("GITHUB_TOKEN")
      }
    }
  }

  publications {
    // Kotlin JVM ("org.jetbrains.kotlin.jvm")
    pluginManager.withPlugin("java") {
      register<MavenPublication>("maven") {
        from(components["java"])
        configurePom()
      }

      // Add an executable artifact if exists
      withType<MavenPublication>().configureEach {
        // val execJar = tasks.findByName("buildExecutable") as? ReallyExecJar
        // if (execJar != null) {
        //   artifact(execJar.execJarFile)
        // }
      }
    }

    // Java Platform (BOM)
    pluginManager.withPlugin("java-platform") {
      register<MavenPublication>("maven") {
        from(components["javaPlatform"])
        configurePom()
      }
    }

    // Gradle version catalog
    pluginManager.withPlugin("version-catalog") {
      register<MavenPublication>("maven") {
        from(components["versionCatalog"])
        configurePom()
      }
    }

    // Add Dokka html doc to all publications
    pluginManager.withPlugin("org.jetbrains.dokka") {
      val dokkaHtmlJar by
          tasks.registering(Jar::class) {
            from(tasks.named("dokkaHtml"))
            archiveClassifier = "html-docs"
          }

      withType<MavenPublication>().configureEach { artifact(dokkaHtmlJar) }
    }
  }
}

signing {
  setRequired { hasSigningKey }
  if (hasSigningKey) {
    useInMemoryPgpKeys(signingKeyId.orNull, signingKey.orNull, signingPassword.orNull)
  }
  sign(publishing.publications)
  // gradle.taskGraph.allTasks.any { it.name.startsWith("publish") }
}

nmcp {
  publishAllPublications {
    username = mavenCentralUsername
    password = mavenCentralPassword
  }
}

fun MavenPublication.configurePom() {
  pom {
    name = provider { "${project.group}:${project.name}" }
    description = provider { project.description }
    inceptionYear = "2024"
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

  // For maven central portal publications - https://github.com/gradle/gradle/issues/26091
  withType<NmcpPublishTask>().configureEach { mustRunAfter(withType<Sign>()) }

  withType<AbstractPublishToMaven>().configureEach { mustRunAfter(withType<Sign>()) }
}
