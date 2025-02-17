package testbench.plugins.network

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.datetime.Clock
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import testbench.plugin.client.ClientPlugin
import kotlin.random.Random

public class OkHttpNetworkClientPlugin :
    NetworkPlugin(),
    ClientPlugin<Unit, NetworkPluginMessage>,
    Interceptor {
    private val messageQueue = Channel<NetworkPluginMessage>(
        capacity = Int.MAX_VALUE,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    override val outgoingMessages: Flow<NetworkPluginMessage> = messageQueue.receiveAsFlow()

    override suspend fun handleMessage(message: Unit) {
    }

    @OptIn(ExperimentalStdlibApi::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestId = Random.nextBytes(8).toHexString()
        val requestTime = Clock.System.now()
        val request = chain.request()

        val requestBody = request.body
        val body = when {
            requestBody == null -> {
                null
            }

            requestBody.isDuplex() -> {
                "(duplex request body omitted)"
            }

            requestBody.isOneShot() -> {
                "(one-shot body omitted)"
            }

            requestBody.contentType() == "application/json".toMediaType() -> {
                val buffer = Buffer()
                requestBody.writeTo(buffer)
                buffer.readUtf8()
            }

            else -> {
                null
            }
        }
        messageQueue.trySend(
            NetworkRequestMessage(
                id = requestId,
                url = request.url.toString(),
                method = request.method,
                headers = request.headers.toMultimap(),
                initiatedAt = requestTime,
                body = body,
            ),
        )

        val response = try {
            chain.proceed(request)
        } catch (e: Exception) {
            messageQueue.trySend(
                NetworkResponseMessage.Failed(
                    id = id,
                    message = e.stackTraceToString(),
                ),
            )
            throw e
        }

        val responseBodyString = response.body?.string()
        messageQueue.trySend(
            NetworkResponseMessage.Completed(
                id = requestId,
                headers = response.headers.toMultimap(),
                status = response.code,
                body = responseBodyString,
            ),
        )

        return response
            .newBuilder()
            .apply {
                if (responseBodyString != null) {
                    body(responseBodyString.toResponseBody(response.body?.contentType()))
                }
            }.build()
    }
}
