package testbench.plugin.client

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import testbench.plugin.BenchPlugin

public interface ClientPlugin<ServerMessage : Any, ClientMessage : Any> : BenchPlugin<ServerMessage, ClientMessage> {
    public val outgoingMessages: Flow<ServerMessage>
        get() = emptyFlow()

    public suspend fun handleMessage(message: ClientMessage)
}
