package dev.suresh

fun main() {
  println("Hello, ${BuildConfig.name}! 🏖")
  println("Java: ${BuildConfig.java}, Kotlin: ${BuildConfig.kotlin}")
  println("Runtime Version: ${JApp.runtimeVersion()}")
}
