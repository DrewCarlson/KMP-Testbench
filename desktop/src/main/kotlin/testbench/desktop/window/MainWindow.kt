package testbench.desktop.window

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import testbench.desktop.components.MainContentContainer
import testbench.desktop.components.SidebarContainer
import testbench.desktop.server.SessionData
import testbench.ui.TestbenchTheme
import testbench.plugin.desktop.DesktopPlugin
import testbench.ui.testbench
import java.awt.Toolkit

@Composable
@Preview
fun MainWindow(
    activeSession: SessionData,
    sessions: Map<String, SessionData>,
    onSessionSelected: (sessionId: String) -> Unit,
    onCloseRequest: () -> Unit,
) {
    var activePlugin by remember { mutableStateOf<DesktopPlugin<*, *>?>(null) }

    val windowState = rememberWindowState(
        position = WindowPosition.Aligned(Alignment.Center),
        // size = remember { calculateDefaultWindowSize() },
    )
    LaunchedEffect(activeSession) {
        activePlugin = activeSession.pluginRegistry
            .enabledPlugins
            .firstNotNullOfOrNull { (_, value) ->
                value.takeIf { it.id == activePlugin?.id }
            }
    }
    Window(
        title = "Testbench",
        state = windowState,
        onCloseRequest = onCloseRequest,
    ) {
        MainWindowContent(
            activePlugin = activePlugin,
            onPluginSelected = { activePlugin = it },
            activeSession = activeSession,
            sessions = sessions,
            onSessionSelected = onSessionSelected,
        )
    }
}

@Composable
fun MainWindowContent(
    activePlugin: DesktopPlugin<*, *>?,
    onPluginSelected: (plugin: DesktopPlugin<*, *>) -> Unit,
    activeSession: SessionData,
    sessions: Map<String, SessionData>,
    onSessionSelected: (id: String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TestbenchTheme.colors.background),
    ) {
        Row(
            modifier = Modifier
                .weight(1f),
        ) {
            SidebarContainer(
                modifier = Modifier
                    .width(200.dp)
                    .fillMaxHeight(),
                onPluginSelected = onPluginSelected,
                onSessionSelected = onSessionSelected,
                activeSession = activeSession,
            )

            testbench.VerticalDivider()

            MainContentContainer(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                activePlugin = activePlugin,
            )
        }
    }
}

private fun calculateDefaultWindowSize(): DpSize {
    val screenSize = Toolkit.getDefaultToolkit().screenSize
    val screenWidth = screenSize.width

    val targetWidth = (screenWidth * 0.8).toInt()
    val targetHeight = (targetWidth / 16.0 * 10.0).toInt()

    return DpSize(targetWidth.dp, targetHeight.dp)
}
