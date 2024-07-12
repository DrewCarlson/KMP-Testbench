package testbench.plugins.logs

import testbench.plugin.desktop.DesktopPlugin
import testbench.plugin.desktop.UiHook
import testbench.plugin.desktop.UiHookLocation
import testbench.plugin.desktop.registerUi

public class LogsPlaceholderPlugin : DesktopPlugin<String, String> {
    override val id: String = "logs"
    override val name: String = "Logs"

    override val requiresClient: Boolean = false

    override val uiHooks: Map<UiHookLocation, UiHook> = registerUi {
        addMainPanel { modifier ->
            LogsMainPanel(
                entries = emptyList(),
                modifier = modifier,
            )
        }
    }

    override suspend fun handleMessage(message: String) {
    }
}
