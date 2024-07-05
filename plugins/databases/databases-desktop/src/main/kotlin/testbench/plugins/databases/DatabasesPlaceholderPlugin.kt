package testbench.plugins.databases

import org.jetbrains.jewel.ui.component.Text
import testbench.plugin.desktop.DesktopPlugin
import testbench.plugin.desktop.UiHook
import testbench.plugin.desktop.UiHookLocation
import testbench.plugin.desktop.registerUi

public class DatabasesPlaceholderPlugin : DesktopPlugin<String, String> {
    override val id: String = "databases"
    override val name: String = "Databases"
    override val pluginIcon: String = "database"

    override val uiHooks: Map<UiHookLocation, UiHook> = registerUi {
        addMainPanel {
            Text(text = "$name Placeholder")
        }
    }

    override suspend fun handleMessage(message: String) {
    }
}
