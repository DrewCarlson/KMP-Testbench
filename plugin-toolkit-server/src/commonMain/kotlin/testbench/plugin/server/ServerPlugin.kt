package testbench.plugin.server

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import testbench.plugin.BenchPlugin

public interface ServerPlugin<ServerMessage : Any, ClientMessage : Any> : BenchPlugin<ServerMessage, ClientMessage> {
    public fun handleMessage(message: ServerMessage)

    @Composable
    public fun renderPanel(modifier: Modifier)
}
