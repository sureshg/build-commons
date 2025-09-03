package dev.suresh

data class Lang(val name: String)

fun main() {
  println("Hello, ${BuildConfig.name}! 🏖")
  println("Java: ${BuildConfig.java}, Kotlin: ${BuildConfig.kotlin}")
  println("Runtime Version: ${JApp.runtimeVersion()}")
  println(Lang("Java").copy(name = "Kotlin"))
}
