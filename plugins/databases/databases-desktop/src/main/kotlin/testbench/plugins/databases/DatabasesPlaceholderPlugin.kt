package testbench.plugins.databases

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.jewel.ui.component.Text
import testbench.plugin.desktop.DesktopPlugin

public class DatabasesPlaceholderPlugin : DesktopPlugin<String, String> {
    override val id: String = "databases"
    override val name: String = "Databases"
    override val pluginIcon: String = "database"

    override suspend fun handleMessage(message: String) {
    }

    @Composable
    override fun renderPanel(modifier: Modifier) {
        Text("Placeholder")
    }
}
