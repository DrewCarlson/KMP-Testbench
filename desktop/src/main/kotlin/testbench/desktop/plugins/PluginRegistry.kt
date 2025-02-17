package testbench.desktop.plugins

import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import testbench.communication.PluginMessage
import testbench.desktop.preview.PreviewPlugin
import testbench.device.DeviceInfo
import testbench.plugin.desktop.DesktopPlugin
import java.util.*

class PluginRegistry(
    clientPluginIds: List<String>,
    plugins: List<DesktopPlugin<*, *>>,
    private val deviceInfo: DeviceInfo = DeviceInfo.host,
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

    suspend fun handleMessage(message: PluginMessage) {
        val (pluginId, content) = message
        val plugin = enabledPlugins[pluginId] ?: return
        val pluginMessage = Json.decodeFromString(serializer(plugin.clientMessageType), content)
        @Suppress("UNCHECKED_CAST")
        (plugin as DesktopPlugin<Any, Any>).handleMessage(pluginMessage!!)
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
