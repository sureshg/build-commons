package plugins

import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.api.services.*
import org.gradle.kotlin.dsl.*

/**
 * For more details on how to use, refer
 * [Stifle Hungry Tasks using BuildService](https://www.liutikas.net/2025/02/06/Stifle-Hungry-Tasks.html)
 */
abstract class LimitingBuildService : BuildService<BuildServiceParameters.None> {
  companion object {
    const val KEY = "LimitingBuildService"
  }
}

/**
 * Use the service as a Gradle managed lock to limit the concurrency. To use this service,
 * ```kotlin
 * tasks.withType(<Task>::class.java).configureEach {
 *     usesService(limitingService)
 * }
 * ```
 */
val Project.limitingService: Provider<LimitingBuildService>
  get() =
      gradle.sharedServices.registerIfAbsent(
          LimitingBuildService.KEY,
          LimitingBuildService::class.java,
      ) {
        maxParallelUsages = 2
      }
