package testbench.desktop.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import networkPlugin
import org.jetbrains.jewel.foundation.theme.JewelTheme

@Composable
fun MainContentContainer(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .background(JewelTheme.globalColors.panelBackground)
    ) {
        networkPlugin.renderPanel(Modifier.fillMaxSize())

    }
}