package testbench.desktop.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.jewel.foundation.theme.JewelTheme
import testbench.plugin.desktop.DesktopPlugin
import testbench.plugin.desktop.UiHookLocation
import testbench.testbench.desktop.components.WelcomePanel

@Composable
fun MainContentContainer(
    modifier: Modifier = Modifier,
    activePlugin: DesktopPlugin<*, *>?,
) {
    val uiHook = remember(activePlugin) {
        activePlugin?.ui?.get(UiHookLocation.MAIN_PANEL)
    }
    Box(
        modifier = modifier
            .background(JewelTheme.globalColors.panelBackground),
        contentAlignment = Alignment.Center,
    ) {
        uiHook?.invoke(Modifier) ?: WelcomePanel()
    }
}
