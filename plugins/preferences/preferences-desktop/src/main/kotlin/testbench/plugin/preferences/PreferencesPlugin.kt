package testbench.plugin.preferences

import org.jetbrains.jewel.ui.component.Text
import testbench.plugin.desktop.DesktopPlugin
import testbench.plugin.desktop.UiHook
import testbench.plugin.desktop.UiHookLocation
import testbench.plugin.desktop.registerUi

public class PreferencesPlugin : DesktopPlugin<String, String> {
    override val id: String = "preferences"
    override val name: String = "Preferences"

    override val uiHooks: Map<UiHookLocation, UiHook> = registerUi {
        addMainPanel {
            Text(text = "$name Placeholder")
        }
    }

    override suspend fun handleMessage(message: String) {
    }
}
