package testbench.plugin.desktop

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import testbench.device.DevicePlatform
import testbench.plugin.BenchPlugin

public interface DesktopPlugin<ServerMessage : Any, ClientMessage : Any> : BenchPlugin<ServerMessage, ClientMessage> {
    public val outgoingMessages: Flow<ServerMessage>
        get() = emptyFlow()

    public val supportedPlatforms: List<DevicePlatform>
        get() = DevicePlatform.entries

    public val requiresClient: Boolean
        get() = true

    public val ui: UiHooks
        get() = UiHooks(emptyMap())

    public suspend fun handleMessage(message: ClientMessage)
}

@Suppress("UnusedReceiverParameter")
public fun DesktopPlugin<*, *>.UiHooks(block: UiHookMapBuilder.() -> Unit): UiHooks {
    return UiHookMapBuilder().apply(block).build()
}
