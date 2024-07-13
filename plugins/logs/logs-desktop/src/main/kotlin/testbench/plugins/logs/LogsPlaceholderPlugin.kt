package testbench.plugins.logs

import testbench.plugin.desktop.DesktopPlugin
import testbench.plugin.desktop.UiHooks

public class LogsPlaceholderPlugin : DesktopPlugin<String, String> {
    override val id: String = "logs"
    override val name: String = "Logs"

    override val requiresClient: Boolean = false

    override val ui: UiHooks = UiHooks {
        MainPanel { modifier ->
            LogsMainPanel(
                entries = emptyList(),
                modifier = modifier,
            )
        }
    }

    override suspend fun handleMessage(message: String) {
    }
}
