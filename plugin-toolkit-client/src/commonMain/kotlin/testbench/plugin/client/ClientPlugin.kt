package testbench.plugin.client

import kotlinx.coroutines.flow.Flow
import testbench.plugin.BenchPlugin

public interface ClientPlugin<ServerMessage : Any, ClientMessage : Any> : BenchPlugin<ServerMessage, ClientMessage> {
    public val outgoingMessages: Flow<ServerMessage>

    public fun handleMessage(message: ClientMessage)
}
