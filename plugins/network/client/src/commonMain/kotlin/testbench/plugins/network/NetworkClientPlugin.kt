package testbench.plugins.network

import io.ktor.client.*
import io.ktor.client.plugins.api.*
import io.ktor.client.plugins.observer.*
import io.ktor.client.statement.*
import io.ktor.http.content.*
import io.ktor.util.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import testbench.plugin.client.ClientPlugin
import java.util.UUID
import kotlin.reflect.KType
import kotlin.reflect.typeOf

private val KtorRequestId = AttributeKey<String>("KMP-Test-Bench-ID")

// TODO: Move this implementation to Ktor specific plugin and replace
//  it with platform based HTTP hooks
public class NetworkClientPlugin : ClientPlugin<NetworkPluginMessage, Unit> {
    override val id: String = "network"

    override val name: String = "Network"

    private val messageQueue = Channel<NetworkPluginMessage>(
        capacity = Int.MAX_VALUE,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    override val serverMessageType: KType = typeOf<NetworkPluginMessage>()

    override val outgoingMessages: Flow<NetworkPluginMessage> = messageQueue.receiveAsFlow()

    override fun handleMessage(message: Unit) {
    }

    private val ktorPlugin: io.ktor.client.plugins.api.ClientPlugin<Unit> =
        createClientPlugin("KMP-Test-Bench-Plugin") {
            on(SetupRequest) { request ->
                val id = UUID.randomUUID().toString()
                request.attributes.put(KtorRequestId, id)
            }
            on(SendingRequest) { request, content ->
                val message = NetworkRequestMessage(
                    id = request.attributes[KtorRequestId],
                    url = request.url.buildString(),
                    method = request.method.value,
                    headers = request.headers.build().toMap(),
                    body = (content as? TextContent)?.text,
                )
                messageQueue.send(message)
            }
        }

    public fun install(config: HttpClientConfig<*>) {
        config.install(ktorPlugin)
        config.ResponseObserver { response ->
            val message = NetworkResponseMessage(
                id = response.request.attributes[KtorRequestId],
                headers = response.headers.toMap(),
                status = response.status.value,
                body = response.bodyAsText(),
            )
            messageQueue.send(message)
        }
    }
}
