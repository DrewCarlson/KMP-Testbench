package testbench.desktop.preview

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import testbench.desktop.LocalSessionHolder
import testbench.desktop.plugins.PluginRegistry
import testbench.desktop.plugins.PluginRegistry.Companion.loadPlugins
import testbench.desktop.server.SessionData
import testbench.desktop.server.SessionHolder
import testbench.desktop.window.MainWindowContent
import testbench.device.DeviceInfo
import testbench.ui.TestbenchTheme

@Preview
@Composable
private fun AppPreview() {
    CompositionLocalProvider(
        LocalSessionHolder provides SessionHolder(),
    ) {
        TestbenchTheme {
            MainWindowContent(
                sessions = mapOf("default" to defaultSession),
                activePlugin = PreviewPlugin(),
                onPluginSelected = {},
                onSessionSelected = {},
                activeSession = defaultSession,
            )
        }
    }
}

private val defaultSession = SessionData(
    sessionId = "default",
    isConnected = false,
    deviceInfo = DeviceInfo.host,
    pluginRegistry = PluginRegistry(emptyList(), loadPlugins()),
)
