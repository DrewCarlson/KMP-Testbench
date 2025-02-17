package testbench.gradle.settings

import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings

public class TestbenchGradleSettingsPlugin : Plugin<Settings> {
    override fun apply(settings: Settings) {
        settings.extensions.create(
            "testbench",
            TestbenchGradleSettingsExtension::class.java,
            settings,
        )
    }
}
