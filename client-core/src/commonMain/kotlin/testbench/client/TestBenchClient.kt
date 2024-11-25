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
    private val enabledState = MutableStateFlow(autoConnect)

    @OptIn(ExperimentalStdlibApi::class)
    private val sessionId = Random.Default.nextBytes(4).toHexString()

    private val http = scope.async(start = CoroutineStart.LAZY) {
        HttpClient {
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
    }

    init {
        scope.launch { setupClientHandling() }
    }

    public val isConnected: StateFlow<Boolean> = connected.asStateFlow()
    public val isEnabled: StateFlow<Boolean> = enabledState.asStateFlow()

    public fun enable() {
        enabledState.update { true }
    }

    public fun disable() {
        enabledState.update { false }
    }

    private suspend fun setupClientHandling() {
        enabledState.collectLatest { tryConnect ->
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
        } catch (e: CancellationException) {
            throw e
        } catch (e: Throwable) {
            e.printStackTrace()
        } finally {
            connected.update { false }
        }
    }

    private suspend fun createWsConnection(onConnected: () -> Unit) {
        http.await().ws {
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
            closeReason.await()
            cancel()
        }
    }
}
