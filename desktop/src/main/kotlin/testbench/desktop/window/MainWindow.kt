package testbench.desktop.window

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.Orientation
import org.jetbrains.jewel.ui.component.Divider
import org.jetbrains.jewel.window.DecoratedWindow
import testbench.desktop.components.FooterContainer
import testbench.desktop.components.MainContentContainer
import testbench.desktop.components.SidebarContainer
import testbench.desktop.components.TitleBarView
import testbench.plugin.server.ServerPlugin

@Composable
@Preview
fun MainWindow(onCloseRequest: () -> Unit) {
    var activePlugin by remember { mutableStateOf<ServerPlugin?>(null) }
    val windowState = rememberWindowState(
        position = WindowPosition.Aligned(Alignment.Center),
    )
    DecoratedWindow(
        onCloseRequest = onCloseRequest,
        title = "KMP Test Bench",
        state = windowState,
    ) {
        TitleBarView()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(JewelTheme.globalColors.panelBackground),
        ) {
            Row(
                modifier = Modifier
                    .weight(1f),
            ) {
                SidebarContainer(
                    modifier = Modifier
                        .width(200.dp)
                        .fillMaxHeight(),
                    onPluginSelected = { activePlugin = it },
                )
                Divider(
                    orientation = Orientation.Vertical,
                )
                MainContentContainer(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    activePlugin = activePlugin,
                )
            }

            FooterContainer(modifier = Modifier.fillMaxWidth())
        }
    }
}
