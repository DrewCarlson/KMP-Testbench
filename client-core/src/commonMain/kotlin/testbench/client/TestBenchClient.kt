package testbench.client

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.websocket.*
import io.ktor.serialization.kotlinx.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import testbench.communication.ClientConnectMessage
import testbench.communication.PluginMessage
import testbench.device.DeviceInfo
import testbench.device.DevicePlatform
import testbench.plugin.client.ClientPlugin
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

public class TestBenchClient(
    private val plugins: List<ClientPlugin<*, *>>,
    autoConnect: Boolean = true,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val connected = MutableStateFlow(false)
    private val isEnabled = MutableStateFlow(autoConnect)

    @OptIn(ExperimentalStdlibApi::class)
    private val sessionId = Random.Default.nextBytes(4).toHexString()

    private val httpFlow = flow {
        val http = HttpClient {
            defaultRequest {
                if (DeviceInfo.host.platform == DevicePlatform.ANDROID) {
                    url(host = "10.0.2.2", port = 8182)
                } else {
                    url(host = "127.0.0.1", port = 8182)
                }
            }
            WebSockets {
                pingInterval = 60
                contentConverter = KotlinxWebsocketSerializationConverter(Json)
            }
        }
        emit(http)
    }.shareIn(scope, SharingStarted.Lazily, replay = 1)

    init {
        scope.launch { setupClientHandling() }
    }

    public fun enable() {
        isEnabled.update { true }
    }

    public fun disable() {
        isEnabled.update { false }
    }

    private suspend fun http(): HttpClient {
        return httpFlow.first()
    }

    private suspend fun setupClientHandling() {
        isEnabled.collectLatest { tryConnect ->
            if (tryConnect) {
                while (true) {
                    establishConnection()
                    delay(3.seconds)
                }
            } else {
                // Sink plugin messages to prevent queueing while client is disabled
                @OptIn(ExperimentalCoroutinesApi::class)
                plugins
                    .map { it.outgoingMessages }
                    .asFlow()
                    .flattenMerge(concurrency = plugins.size)
                    .collect()
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
        http().ws {
            onConnected()

            sendSerialized(
                ClientConnectMessage(
                    sessionId = sessionId,
                    deviceInfo = DeviceInfo.host,
                    pluginIds = plugins.map { it.id },
                ),
            )

            plugins.forEach { plugin ->
                plugin.outgoingMessages
                    .onEach { content ->
                        val serializedContent = Json.encodeToString(serializer(plugin.serverMessageType), content)
                        sendSerialized(PluginMessage(plugin.id, serializedContent))
                    }.launchIn(this)
            }
            awaitCancellation()
        }
    }
}
