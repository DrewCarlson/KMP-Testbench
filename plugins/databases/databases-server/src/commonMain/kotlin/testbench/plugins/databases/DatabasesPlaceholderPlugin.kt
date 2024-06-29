package testbench.plugins.databases

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.jewel.ui.component.Text
import testbench.plugin.server.ServerPlugin

public class DatabasesPlaceholderPlugin : ServerPlugin<String, String> {
    override val id: String = "testbench/plugins/databases"
    override val name: String = "Databases"
    override val pluginIcon: String = "database"

    override fun handleMessage(message: String) {
    }

    @Composable
    override fun renderPanel(modifier: Modifier) {
        Text("Placeholder")
    }
}
