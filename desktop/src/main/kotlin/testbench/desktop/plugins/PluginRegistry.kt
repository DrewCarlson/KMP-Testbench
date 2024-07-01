package testbench.desktop.plugins

import testbench.plugin.desktop.DesktopPlugin
import java.util.ServiceLoader

class PluginRegistry {
    val plugins: Map<String, DesktopPlugin<*, *>> = ServiceLoader
        .load(DesktopPlugin::class.java)
        .sortedBy { it.name.lowercase() }
        .associateBy { it.id }
}
