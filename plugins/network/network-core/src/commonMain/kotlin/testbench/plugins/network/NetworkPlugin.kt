package testbench.plugins.network

import testbench.plugin.BenchPlugin
import kotlin.reflect.KType
import kotlin.reflect.typeOf

public abstract class NetworkPlugin : BenchPlugin<Unit, NetworkPluginMessage> {
    override val id: String = "network"

    override val name: String = "Network"

    override val pluginIcon: String = "globe"

    override val clientMessageType: KType = typeOf<NetworkPluginMessage>()
}
