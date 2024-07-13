package testbench.plugins.network

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
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
public sealed class NetworkResponseMessage : NetworkPluginMessage() {
    public abstract val id: String

    @Serializable
    public data class Completed(
        override val id: String,
        val headers: Map<String, List<String>>,
        val status: Int,
        val body: String?,
    ) : NetworkResponseMessage()

    @Serializable
    public data class Cancelled(
        override val id: String,
        /** True if the request was cancelled after sending the request, while receiving the response. */
        val sent: Boolean,
    ) : NetworkResponseMessage()

    @Serializable
    public data class Failed(
        override val id: String,
        /** Typically an exception message produced while handling the request. */
        val message: String,
    ) : NetworkResponseMessage()
}
