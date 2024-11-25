package testbench.plugins.databases

import testbench.plugin.desktop.DesktopPlugin
import testbench.plugin.desktop.UiHooks
import testbench.ui.testbench

public class DatabasesPlaceholderPlugin : DesktopPlugin<String, String> {
    override val id: String = "databases"
    override val name: String = "Databases"
    override val pluginIcon: String = "database"

    override val ui: UiHooks = UiHooks {
        MainPanel {
            testbench.Text(text = "$name Placeholder")
        }
    }

    override suspend fun handleMessage(message: String) {
    }
}
