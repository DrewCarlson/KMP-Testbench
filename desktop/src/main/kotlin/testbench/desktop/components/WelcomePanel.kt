package testbench.testbench.desktop.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.jewel.ui.component.Text

@Composable
fun WelcomePanel(modifier: Modifier = Modifier) {
    // TODO: Better default welcome screen
    Text(text = "Welcome to KMP Testbench!")
}
