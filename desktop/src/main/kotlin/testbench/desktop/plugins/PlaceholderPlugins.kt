package testbench.desktop.plugins

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.jewel.ui.component.Text
import testbench.plugin.server.ServerPlugin

abstract class PlaceholderPlugin : ServerPlugin<String, String> {
    override fun handleMessage(message: String) {
    }

    @Composable
    override fun renderPanel(modifier: Modifier) {
        Text(text = "$name Placeholder")
    }
}

class DatabasesPlaceholderPlugin : PlaceholderPlugin() {
    override val id: String = "databases"
    override val name: String = "Databases"
    override val pluginIcon: String = "database"
}

class LogsPlaceholderPlugin : PlaceholderPlugin() {
    override val id: String = "logs"
    override val name: String = "Logs"
}

class PreferencesPlaceholderPlugin : PlaceholderPlugin() {
    override val id: String = "preferences"
    override val name: String = "Preferences"
}
