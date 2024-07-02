package testbench.plugins.network

import kotlinx.datetime.Instant
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable

@Serializable
@Polymorphic
public sealed class NetworkPluginMessage

@Serializable
public data class NetworkRequestMessage(
    val id: String,
    val url: String,
    val method: String,
    val headers: Map<String, List<String>>,
    val body: String?,
    val initiatedAt: Instant,
) : NetworkPluginMessage()

@Serializable
public data class NetworkResponseMessage(
    val id: String,
    val headers: Map<String, List<String>>,
    val status: Int,
    val body: String?,
) : NetworkPluginMessage()
