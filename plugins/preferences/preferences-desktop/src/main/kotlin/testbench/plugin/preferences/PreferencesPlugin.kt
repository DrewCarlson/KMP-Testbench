package testbench.plugin.preferences

import org.jetbrains.jewel.ui.component.Text
import testbench.plugin.desktop.DesktopPlugin
import testbench.plugin.desktop.UiHooks

public class PreferencesPlugin : DesktopPlugin<String, String> {
    override val id: String = "preferences"
    override val name: String = "Preferences"

    override val ui: UiHooks = UiHooks {
        MainPanel {
            Text(text = "$name Placeholder")
        }
    }

    override suspend fun handleMessage(message: String) {
    }
}
