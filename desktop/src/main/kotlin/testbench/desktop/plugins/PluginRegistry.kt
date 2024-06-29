package testbench.desktop.plugins

import testbench.plugin.server.ServerPlugin
import java.util.ServiceLoader

class PluginRegistry {
    val plugins: Map<String, ServerPlugin<*, *>> = ServiceLoader
        .load(ServerPlugin::class.java)
        .sortedBy { it.name.lowercase() }
        .associateBy { it.id }
}
