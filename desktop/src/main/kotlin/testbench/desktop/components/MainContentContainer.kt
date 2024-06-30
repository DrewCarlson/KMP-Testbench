package testbench.desktop.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.jewel.foundation.theme.JewelTheme
import testbench.plugin.server.ServerPlugin
import testbench.testbench.desktop.components.WelcomePanel

@Composable
fun MainContentContainer(
    modifier: Modifier = Modifier,
    activePlugin: ServerPlugin<*, *>?,
) {
    Box(
        modifier = modifier
            .background(JewelTheme.globalColors.panelBackground),
        contentAlignment = Alignment.Center,
    ) {
        activePlugin
            ?.renderPanel(Modifier.fillMaxSize())
            ?: WelcomePanel()
    }
}
