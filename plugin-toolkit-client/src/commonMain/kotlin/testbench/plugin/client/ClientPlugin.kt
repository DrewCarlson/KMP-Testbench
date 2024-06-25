package testbench.plugin.client

import kotlinx.coroutines.flow.Flow
import testbench.plugin.BenchPlugin
import kotlin.reflect.KType

public interface ClientPlugin<ServerMessage : Any, ClientMessage : Any> : BenchPlugin {
    public val clientMessageType: KType
        get() = error("${this::class.simpleName} does not provide a client message type")

    public val serverMessageType: KType
        get() = error("${this::class.simpleName} does not provide a server message type")

    public val outgoingMessages: Flow<ServerMessage>

    public fun handleMessage(message: ClientMessage)
}
