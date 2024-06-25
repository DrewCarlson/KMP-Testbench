package testbench.plugin.server

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import testbench.plugin.BenchPlugin
import kotlin.reflect.KType

public interface ServerPlugin<ServerMessage : Any, ClientMessage : Any> : BenchPlugin {
    public val pluginIcon: String
        get() = "plugin"

    public val clientMessageType: KType
        get() = error("${this::class.simpleName} does not provide a client message type")

    public val serverMessageType: KType
        get() = error("${this::class.simpleName} does not provide a server message type")

    public fun handleMessage(message: ServerMessage)

    @Composable
    public fun renderPanel(modifier: Modifier)
}
