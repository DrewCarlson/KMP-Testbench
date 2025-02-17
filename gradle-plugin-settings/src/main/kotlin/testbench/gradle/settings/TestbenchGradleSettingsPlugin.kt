package testbench.gradle.settings

import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.jetbrains.kotlin.gradle.plugin.extraProperties

public class TestbenchGradleSettingsPlugin : Plugin<Settings> {
    override fun apply(settings: Settings) {
        settings.extensions.create(
            "testbench",
            TestbenchGradleSettingsExtension::class.java,
            settings,
        )

        settings.gradle.settingsEvaluated {
            if (settings.gradle.extraProperties.has(TESTBENCH_PLUGIN_MODULE)) {
                @Suppress("UNCHECKED_CAST")
                val text =
                    (settings.gradle.extraProperties.get(TESTBENCH_PLUGIN_MODULE) as List<String>).joinToString("\n")
                val modulesListFile = settings.rootDir.resolve(".gradle/testbench.modules")
                modulesListFile.parentFile.mkdirs()
                modulesListFile.bufferedWriter().use { out ->
                    out.write(text)
                }
            }
        }
    }
}
