package testbench.plugins.network

import io.ktor.client.*
import io.ktor.client.plugins.api.*
import io.ktor.client.plugins.observer.*
import io.ktor.client.statement.*
import io.ktor.client.utils.*
import io.ktor.http.content.*
import io.ktor.util.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import testbench.plugin.client.ClientPlugin
import kotlin.coroutines.cancellation.CancellationException
import kotlin.random.Random

private val KtorRequestId = AttributeKey<String>("KMP-Testbench-ID")
private val KtorRequestTime = AttributeKey<Instant>("KMP-Testbench-Time")

public class KtorNetworkClientPlugin :
    NetworkPlugin(),
    ClientPlugin<NetworkPluginMessage, Unit> {
    private val messageQueue = Channel<NetworkPluginMessage>(
        capacity = Int.MAX_VALUE,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    override val outgoingMessages: Flow<NetworkPluginMessage> = messageQueue.receiveAsFlow()

    override suspend fun handleMessage(message: Unit) {
    }

    @OptIn(ExperimentalStdlibApi::class)
    private val ktorPlugin: io.ktor.client.plugins.api.ClientPlugin<Unit> =
        createClientPlugin("KMP-Testbench-Plugin") {
            on(SetupRequest) { request ->
                request.attributes.put(KtorRequestId, Random.Default.nextBytes(8).toHexString())
                request.attributes.put(KtorRequestTime, Clock.System.now())
            }
            on(SendingRequest) { request, content ->
                val message = NetworkRequestMessage(
                    id = request.attributes[KtorRequestId],
                    url = request.url.buildString(),
                    method = request.method.value,
                    headers = request.headers.build().toMap(),
                    initiatedAt = request.attributes[KtorRequestTime],
                    body = (content as? TextContent)?.text,
                )
                messageQueue.send(message)
            }
            on(Send) { request ->
                try {
                    proceed(request)
                } catch (e: Throwable) {
                    val id = request.attributes[KtorRequestId]
                    val message = if ((e.cause ?: e) is CancellationException) {
                        NetworkResponseMessage.Cancelled(
                            id = id,
                            sent = false,
                        )
                    } else {
                        NetworkResponseMessage.Failed(
                            id = id,
                            message = e.stackTraceToString(),
                        )
                    }
                    messageQueue.trySend(message)
                    throw e
                }
            }
            on(MonitoringEvent(HttpResponseCancelled)) { response ->
                val message = NetworkResponseMessage.Cancelled(
                    id = response.request.attributes[KtorRequestId],
                    sent = true,
                )
                messageQueue.trySend(message)
            }
        }

    public fun install(config: HttpClientConfig<*>) {
        config.install(ktorPlugin)
        config.ResponseObserver { response ->
            val message = NetworkResponseMessage.Completed(
                id = response.request.attributes[KtorRequestId],
                headers = response.headers.toMap(),
                status = response.status.value,
                body = response.bodyAsText(),
            )
            messageQueue.send(message)
        }
    }
}
