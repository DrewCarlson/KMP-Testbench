package testbench.plugins.logs

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.jewel.ui.component.Text
import testbench.plugin.server.ServerPlugin

public class LogsPlaceholderPlugin : ServerPlugin<String, String> {
    override val id: String = "logs"
    override val name: String = "Logs"

    override fun handleMessage(message: String) {
    }

    @Composable
    override fun renderPanel(modifier: Modifier) {
        Text(text = "$name Placeholder")
    }
}
