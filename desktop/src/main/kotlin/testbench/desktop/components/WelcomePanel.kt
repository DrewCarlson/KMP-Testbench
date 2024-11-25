package testbench.desktop.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import testbench.desktop.theme.components

@Composable
fun WelcomePanel(modifier: Modifier = Modifier) {
    // TODO: Better default welcome screen
    components.Label(text = "Welcome to KMP Testbench!")
}
