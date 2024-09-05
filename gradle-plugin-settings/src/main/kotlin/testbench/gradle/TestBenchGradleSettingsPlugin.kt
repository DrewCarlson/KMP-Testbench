package testbench.gradle

import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.kotlin.dsl.maven
import org.jetbrains.kotlin.gradle.plugin.extraProperties

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
                maven("https://www.jetbrains.com/intellij-repository/releases")
                maven("https://packages.jetbrains.team/maven/p/kpm/public/")
                maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
            }
        settings.gradle.settingsEvaluated {
            if (settings.gradle.extraProperties.has(TEST_BENCH_PLUGIN_MODULE)) {
                @Suppress("UNCHECKED_CAST")
                val text =
                    (settings.gradle.extraProperties.get(TEST_BENCH_PLUGIN_MODULE) as List<String>).joinToString("\n")
                val modulesListFile = settings.rootDir.resolve(".gradle/testbench.modules")
                modulesListFile.parentFile.mkdirs()
                modulesListFile.bufferedWriter().use { out ->
                    out.write(text)
                }
            }
        }
    }
}
