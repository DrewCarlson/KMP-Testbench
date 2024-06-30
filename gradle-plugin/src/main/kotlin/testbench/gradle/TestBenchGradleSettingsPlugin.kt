package testbench.gradle

import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.kotlin.dsl.maven

public class TestBenchGradleSettingsPlugin : Plugin<Settings> {
    override fun apply(settings: Settings) {
        settings.extensions.create(
            "testbench",
            TestBenchGradleSettingsExtension::class.java,
            settings,
        )

        settings.dependencyResolutionManagement
            .repositories {
                maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
                maven("https://packages.jetbrains.team/maven/p/kpm/public/")
                maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
            }
    }
}
