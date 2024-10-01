package plugins

import common.*
import java.net.URI
import org.jetbrains.dokka.DokkaConfiguration.Visibility
import org.jetbrains.dokka.base.DokkaBase
import org.jetbrains.dokka.base.DokkaBaseConfiguration
import org.jetbrains.dokka.gradle.DokkaMultiModuleTask
import org.jetbrains.dokka.gradle.DokkaTaskPartial

plugins {
  org.jetbrains.dokka
  com.diffplug.spotless
  `test-report-aggregation`
}

// The following plugins and config apply only to a root project.
if (isRootProject) {

  // Combined test report
  dependencies {
    allprojects.filter { !it.path.contains(":catalog") }.forEach { testReportAggregation(it) }
  }

  // Dokka multi-module config.
  tasks.withType<DokkaMultiModuleTask>().configureEach {
    description = project.description.orEmpty()
    moduleName = project.name
    pluginConfiguration<DokkaBase, DokkaBaseConfiguration> {
      footerMessage = "Copyright &copy; 2024 suresh.dev"
      homepageLink = githubRepo
      separateInheritedMembers = false
      mergeImplicitExpectActualDeclarations = false
    }
  }
}

spotless {
  java {
    palantirJavaFormat(libs.versions.palantir.javaformat.get()).formatJavadoc(true)
    target("**/*.java_disabled")
    targetExclude("**/build/**")
  }

  val ktfmtVersion = libs.versions.ktfmt.get()
  kotlin {
    ktfmt(ktfmtVersion)
    target("**/*.kt")
    targetExclude("**/build/**", "**/Service.kt")
    trimTrailingWhitespace()
    endWithNewline()
    // licenseHeader(rootProject.file("gradle/license-header.txt"))
  }

  kotlinGradle {
    ktfmt(ktfmtVersion)
    target("**/*.gradle.kts")
    targetExclude("**/build/**")
    trimTrailingWhitespace()
    endWithNewline()
  }

  format("misc") {
    target("**/*.md", "**/.gitignore", "**/.kte")
    targetExclude("**/build/**")
    trimTrailingWhitespace()
    indentWithSpaces(2)
    endWithNewline()
  }
}

tasks {
  withType<DokkaTaskPartial>().configureEach {
    dokkaSourceSets.configureEach {
      moduleName = project.name
      jdkVersion = kotlinDslJvmTarget.map { it.toInt() }
      noStdlibLink = false
      noJdkLink = false
      reportUndocumented = false
      skipDeprecated = true
      // includes.from("README.md")
      documentedVisibilities = setOf(Visibility.PUBLIC, Visibility.PROTECTED)

      sourceLink {
        localDirectory = rootProject.projectDir
        remoteUrl = URI("${githubRepo}/tree/main").toURL()
        remoteLineSuffix = "#L"
      }

      samples.from("src/test/kotlin")

      perPackageOption {
        matchingRegex = ".*internal.*"
        suppress = true
      }
    }

    pluginConfiguration<DokkaBase, DokkaBaseConfiguration> {
      footerMessage = "Copyright &copy; 2024 suresh.dev"
      homepageLink = githubRepo
    }
  }
}
