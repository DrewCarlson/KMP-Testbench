package testbench.plugin.desktop

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import testbench.device.DevicePlatform
import testbench.plugin.BenchPlugin

public interface DesktopPlugin<ServerMessage : Any, ClientMessage : Any> : BenchPlugin<ServerMessage, ClientMessage> {
    public val outgoingMessages: Flow<ClientMessage>
        get() = emptyFlow()

    public val supportedPlatforms: List<DevicePlatform>
        get() = DevicePlatform.entries

    public val requiresClient: Boolean
        get() = true

    public val uiHooks: Map<UiHookLocation, UiHook>
        get() = emptyMap()

    public suspend fun handleMessage(message: ServerMessage)
}
