@file:Suppress("UnstableApiUsage")

import common.libs

plugins { `version-catalog` }

group = libs.versions.group.get()

catalog {
  versionCatalog {
    // version("kotlin", kotlinVersion.get())
    // library("kotlin-stdlib", "org.jetbrains.kotlin", "kotlin-stdlib").versionRef("kotlin")
    from(files(layout.settingsDirectory.file("gradle/libs.versions.toml")))
  }
}
