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
import testbench.communication.PluginMessage
import testbench.plugin.client.ClientPlugin
import kotlin.time.Duration.Companion.seconds

public class TestBenchClient(
    private val plugins: List<ClientPlugin>,
) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val http = HttpClient {
        defaultRequest {
            url(host = "127.0.0.1", port = 8182)
        }
        WebSockets {
            pingInterval = 60
            contentConverter = KotlinxWebsocketSerializationConverter(Json)
        }
    }

    private val _connected = MutableStateFlow(false)

    init {
        scope.launch {
            while (true) {
                establishConnection()
            }
        }
    }

    private suspend fun establishConnection() {
        _connected.first { connected -> !connected }

        try {
            createWsConnection {
                _connected.update { true }
            }
            _connected.update { false }
        } catch (e: Throwable) {
            e.printStackTrace()
            _connected.update { false }
            delay(3.seconds)
        }
    }

    private suspend fun createWsConnection(onConnected: () -> Unit) {
        http.ws("/plugin") {
            onConnected()

            plugins.forEach { plugin ->
                plugin.outgoingMessages
                    .onEach { println("here: $it") }
                    .onEach {
                        val message = PluginMessage(plugin.id, it)
                        outgoing.send(Frame.Text(Json.encodeToString(message)))
                    }
                    .launchIn(this)
            }
            while (isActive) {
                yield()
            }
        }
    }
}