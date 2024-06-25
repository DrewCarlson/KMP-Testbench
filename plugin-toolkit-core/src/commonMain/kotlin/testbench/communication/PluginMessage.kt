package testbench.communication

import kotlinx.serialization.Serializable

@Serializable
public data class PluginMessage(
    val pluginId: String,
    val content: String,
)
