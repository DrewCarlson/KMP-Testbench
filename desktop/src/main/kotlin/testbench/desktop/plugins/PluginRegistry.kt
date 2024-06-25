package testbench.desktop.plugins

import testbench.plugin.server.ServerPlugin

class PluginRegistry(
    plugins: List<ServerPlugin<*, *>>,
) {
    val plugins = plugins.associateBy { it.id }
}
