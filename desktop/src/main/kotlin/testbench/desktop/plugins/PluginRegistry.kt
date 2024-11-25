package testbench.desktop.plugins

import testbench.desktop.preview.PreviewPlugin
import testbench.device.DeviceInfo
import testbench.plugin.desktop.DesktopPlugin
import java.util.*

class PluginRegistry(
    clientPluginIds: List<String>,
    private val deviceInfo: DeviceInfo = DeviceInfo.host,
    plugins: List<DesktopPlugin<*, *>> = loadPlugins(),
) {
    val disabledPlugins: List<DisabledPlugin> =
        plugins
            .mapDisabledPlugins(clientPluginIds)
            .sortedBy { it.plugin.name.lowercase() }

    val enabledPlugins: Map<String, DesktopPlugin<*, *>> = run {
        val disabledPluginIds = disabledPlugins.map { it.plugin.id }
        plugins
            .filter { !disabledPluginIds.contains(it.id) }
            .sortedBy { it.name.lowercase() }
            .associateBy { it.id }
    }

    private fun List<DesktopPlugin<*, *>>.mapDisabledPlugins(clientPluginIds: List<String>): List<DisabledPlugin> {
        return mapNotNull { plugin ->
            if (plugin.supportedPlatforms.contains(deviceInfo.platform)) {
                when {
                    !plugin.requiresClient -> null

                    clientPluginIds.isEmpty() ||
                        clientPluginIds.contains(plugin.id) -> null

                    else -> DisabledPlugin(plugin, DisabledPlugin.Reason.MISSING_CLIENT)
                }
            } else {
                DisabledPlugin(plugin, DisabledPlugin.Reason.UNSUPPORTED)
            }
        }
    }

    companion object {
        fun loadPlugins(): List<DesktopPlugin<*, *>> {
            return ServiceLoader.load(DesktopPlugin::class.java).toList() + PreviewPlugin()
        }
    }
}
