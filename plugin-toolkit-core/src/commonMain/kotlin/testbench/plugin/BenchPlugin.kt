package testbench.plugin

import kotlin.reflect.KType

public interface BenchPlugin<ServerMessage : Any, ClientMessage : Any> {
    public val id: String

    public val name: String

    public val pluginIcon: String
        get() = "plugin"

    public val clientMessageType: KType
        get() = error("${this::class.simpleName} does not provide a client message type")

    public val serverMessageType: KType
        get() = error("${this::class.simpleName} does not provide a server message type")
}
