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
import kotlin.coroutines.CoroutineContext
import kotlin.random.Random

/**
 * TestBenchClient manages your plugins and connects them to the Testbench Desktop App.
 *
 * [TestBenchClient] itself provides limited public APIs, aiming to be a simple manager
 * for the Desktop App connection.  It will maintain the desired connection state and
 * forward all plugin messages to/from the plugins on the client and Desktop App.
 *
 * @param plugins The list of [ClientPlugin]s that will be enabled for this client.
 * @param autoConnect When true (default), automatically attempt to connect to the desktop app.
 * @param reconnectHandler An optional [ReconnectHandler] to customize reconnection behavior.
 * @param coroutineContext A [CoroutineContext] to customize how the client manages async work, default [Dispatchers.Default].
 */
public class TestBenchClient(
    private val plugins: List<ClientPlugin<*, *>>,
    autoConnect: Boolean = true,
    private val reconnectHandler: ReconnectHandler = ReconnectHandler.default(),
    coroutineContext: CoroutineContext = Dispatchers.Default,
) {
    private val scope = CoroutineScope(SupervisorJob() + coroutineContext)

    // Tracks the actual network connection state, regardless of the expected state.
    private val connected = MutableStateFlow(false)

    // Tracks the desired network connection state.
    private val enabledState = MutableStateFlow(autoConnect)

    @OptIn(ExperimentalStdlibApi::class)
    private val sessionId = Random.Default.nextBytes(4).toHexString()

    // HttpClient may read disk to find the client engine so create it
    // async in case the TestbenchClient is created on the main thread.
    // This is also done lazily because the client may never be connected.
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

    public val isConnectedFlow: StateFlow<Boolean> = connected.asStateFlow()
    public val isEnabledFlow: StateFlow<Boolean> = enabledState.asStateFlow()

    public val isConnected: Boolean
        get() = connected.value
    public val isEnabled: Boolean
        get() = enabledState.value

    public fun enable() {
        scope.launch { enabledState.emit(true) }
    }

    public fun disable() {
        scope.launch { enabledState.emit(false) }
    }

    private suspend fun setupClientHandling() {
        enabledState.collectLatest { tryConnect ->
            if (tryConnect) {
                var attempt = 0
                while (true) {
                    establishConnection(onConnected = { attempt = 0 })
                    reconnectHandler.awaitTimeout(++attempt)
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

    private suspend fun establishConnection(onConnected: () -> Unit) {
        // Wait until disconnected
        connected.first { connected -> !connected }

        try {
            // Attempt to connect to server
            createWsConnection(
                onConnected = {
                    onConnected()
                    connected.emit(true)
                },
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Throwable) {
            e.printStackTrace()
        } finally {
            connected.emit(false)
        }
    }

    private suspend fun createWsConnection(onConnected: suspend () -> Unit) {
        http.await().ws {
            onConnected()
            sendConnectMessage()
            sendOutgoingMessages()
            closeReason.await()
            cancel()
        }
    }

    private suspend fun DefaultClientWebSocketSession.sendConnectMessage() {
        sendSerialized(
            ClientConnectMessage(
                sessionId = sessionId,
                deviceInfo = DeviceInfo.host,
                pluginIds = plugins.map { it.id },
            ),
        )
    }

    private fun DefaultClientWebSocketSession.sendOutgoingMessages() {
        plugins.forEach { plugin ->
            plugin.outgoingMessages
                .onEach { message -> sendSerialized(plugin.serializeMessage(message)) }
                .launchIn(this)
        }
    }

    private fun ClientPlugin<*, *>.serializeMessage(content: Any): PluginMessage {
        return PluginMessage(
            pluginId = id,
            content = Json.encodeToString(serializer(serverMessageType), content),
        )
    }
}
