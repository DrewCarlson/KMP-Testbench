package testbench.plugin.desktop

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import testbench.plugin.BenchPlugin

public interface DesktopPlugin<ServerMessage : Any, ClientMessage : Any> : BenchPlugin<ServerMessage, ClientMessage> {
    public val outgoingMessages: Flow<ClientMessage>
        get() = emptyFlow()

    public suspend fun handleMessage(message: ServerMessage)

    @Composable
    public fun renderPanel(modifier: Modifier)
}
