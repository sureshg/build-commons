@file:Suppress("UnstableApiUsage")

package settings

import com.gradle.develocity.agent.gradle.scan.PublishedBuildScan
import common.GithubAction
import org.gradle.api.JavaVersion.VERSION_17
import org.gradle.kotlin.dsl.*
import org.gradle.toolchains.foojay.FoojayToolchainResolver
import org.tomlj.Toml

val versionCatalog by lazy {
  // A hack to read version catalog from settings
  Toml.parse(file("$rootDir/gradle/libs.versions.toml").readText()).getTable("versions")
      ?: error("Unable to parse the version catalog!")
}

pluginManagement {
  require(JavaVersion.current().isCompatibleWith(VERSION_17)) {
    "This build requires Gradle to be run with at least Java $VERSION_17"
  }

  repositories {
    mavenCentral()
    gradlePluginPortal()
    mavenSnapshot()
  }
}

// Apply the plugins to all projects
plugins {
  id("com.gradle.develocity")
  id("org.gradle.toolchains.foojay-resolver")
  id("com.javiersc.semver")
}

// Centralizing repositories declaration
@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
  repositories {
    mavenCentral()
    gradlePluginPortal()
    mavenSnapshot()
  }

  // Enable back after the KMP Node.js repo fix.
  // repositoriesMode = RepositoriesMode.PREFER_SETTINGS
}

@Suppress("UnstableApiUsage")
toolchainManagement {
  jvm {
    javaRepositories {
      repository("foojay") { resolverClass = FoojayToolchainResolver::class.java }
    }
  }
}

develocity {
  buildScan {
    termsOfUseUrl = "https://gradle.com/terms-of-service"
    termsOfUseAgree = "yes"

    capture {
      buildLogging = false
      testLogging = false
    }

    obfuscation {
      ipAddresses { it.map { _ -> "0.0.0.0" } }
      hostname { "*******" }
      username { it.reversed() }
    }

    publishing.onlyIf { GithubAction.isEnabled }
    uploadInBackground = false
    tag("GITHUB_ACTION")
    buildScanPublished { addJobSummary() }
  }
}

fun RepositoryHandler.mavenSnapshot() {
  val mvnSnapshot = providers.gradleProperty("enableMavenSnapshot").orNull.toBoolean()
  if (mvnSnapshot) {
    logger.lifecycle("❖ Maven Snapshot is enabled!")
    maven(url = versionCatalog.getString("repo-mvn-snapshot").orEmpty()) {
      mavenContent { snapshotsOnly() }
    }
  }
}

/** Add build scan details to the GitHub Job summary report! */
fun PublishedBuildScan.addJobSummary() =
    with(GithubAction) {
      setOutput("build_scan_uri", buildScanUri)
      addJobSummary(
          """
          | ##### 🚀 Gradle BuildScan [URL](${buildScanUri.toASCIIString()})
          """
              .trimMargin())
    }

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

enableFeaturePreview("STABLE_CONFIGURATION_CACHE")
