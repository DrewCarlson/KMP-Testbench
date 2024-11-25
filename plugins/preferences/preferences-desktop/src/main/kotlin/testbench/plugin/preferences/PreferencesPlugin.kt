package testbench.plugin.preferences

import testbench.plugin.desktop.DesktopPlugin
import testbench.plugin.desktop.UiHooks
import testbench.ui.testbench

public class PreferencesPlugin : DesktopPlugin<String, String> {
    override val id: String = "preferences"
    override val name: String = "Preferences"

    override val ui: UiHooks = UiHooks {
        MainPanel {
            testbench.Text(text = "$name Placeholder")
        }
    }

    override suspend fun handleMessage(message: String) {
    }
}
