package testbench.plugin.preferences

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.jewel.ui.component.Text
import testbench.plugin.desktop.DesktopPlugin

public class PreferencesPlugin : DesktopPlugin<String, String> {
    override val id: String = "preferences"
    override val name: String = "Preferences"

    override suspend fun handleMessage(message: String) {
    }

    @Composable
    override fun renderPanel(modifier: Modifier) {
        Text(text = "$name Placeholder")
    }
}
