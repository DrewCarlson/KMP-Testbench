package testbench.client

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.websocket.*
import io.ktor.serialization.kotlinx.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import testbench.communication.PluginMessage
import testbench.plugin.client.ClientPlugin
import kotlin.time.Duration.Companion.seconds

public class TestBenchClient(
    private val plugins: List<ClientPlugin<*, *>>,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val connected = MutableStateFlow(false)

    private val http = HttpClient {
        defaultRequest {
            url(host = "127.0.0.1", port = 8182)
        }
        WebSockets {
            pingInterval = 60
            contentConverter = KotlinxWebsocketSerializationConverter(Json)
        }
    }

    init {
        scope.launch {
            while (true) {
                establishConnection()
                delay(3.seconds)
            }
        }
    }

    private suspend fun establishConnection() {
        // Wait until disconnected
        connected.first { connected -> !connected }

        try {
            // Attempt to connect to server
            createWsConnection {
                connected.update { true }
            }
            connected.update { false }
        } catch (e: Throwable) {
            e.printStackTrace()
            connected.update { false }
        }
    }

    private suspend fun createWsConnection(onConnected: () -> Unit) {
        http.ws {
            onConnected()

            plugins.forEach { plugin ->
                plugin.outgoingMessages
                    .onEach { content ->
                        val serializedContent = Json.encodeToString(serializer(plugin.serverMessageType), content)
                        val message = PluginMessage(plugin.id, serializedContent)
                        outgoing.send(Frame.Text(Json.encodeToString(message)))
                    }.launchIn(this)
            }
            awaitCancellation()
        }
    }
}
