package testbench.gradle

import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings

public class TestBenchGradleSettingsPlugin : Plugin<Settings> {
    override fun apply(settings: Settings) {
        settings.extensions.create(
            "testbench",
            TestBenchGradleSettingsExtension::class.java,
            settings,
        )
    }
}
