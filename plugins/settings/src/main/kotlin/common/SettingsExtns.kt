package common

import org.gradle.api.initialization.Settings
import org.gradle.api.provider.Provider

val Settings.isNativeTargetEnabled: Boolean
  get() = gradleBooleanProperty("kotlin.target.native.enabled").get()

fun Settings.gradleBooleanProperty(name: String): Provider<Boolean> =
    providers.gradleProperty(name).map(String::toBoolean).orElse(false)
