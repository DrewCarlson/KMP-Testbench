package testbench.plugins.network

import testbench.plugin.BenchPlugin
import kotlin.reflect.KType
import kotlin.reflect.typeOf

public abstract class NetworkPlugin : BenchPlugin<NetworkPluginMessage, Unit> {
    override val id: String = "network"

    override val name: String = "Network"

    override val pluginIcon: String = "globe"

    override val serverMessageType: KType = typeOf<NetworkPluginMessage>()
}
