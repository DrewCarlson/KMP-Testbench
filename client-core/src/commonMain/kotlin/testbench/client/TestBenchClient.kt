package testbench.client

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import testbench.communication.ClientConnectMessage
import testbench.communication.DesktopConnectMessage
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
 * @param serverUrl The Desktop App server URL to pair with, for testing only.
 */
public class TestBenchClient(
    private val plugins: List<ClientPlugin<*, *>>,
    autoConnect: Boolean = true,
    private val reconnectHandler: ReconnectHandler = ReconnectHandler.default(),
    coroutineContext: CoroutineContext = Dispatchers.Default,
    private val serverUrl: Url = Url("http://127.0.0.1:8182"),
) {
    private val scope = CoroutineScope(SupervisorJob() + coroutineContext)

    // Tracks the actual network connection state, regardless of the expected state.
    private val connected = MutableStateFlow(false)

    // Tracks the desired network connection state.
    private val enabledState = MutableStateFlow(autoConnect)

    @OptIn(ExperimentalStdlibApi::class)
    private val sessionId = Random.Default.nextBytes(4).toHexString()
    private val pluginMap = plugins.associateBy { it.id }

    // HttpClient may read disk to find the client engine so create it
    // async in case the TestbenchClient is created on the main thread.
    // This is also done lazily because the client may never be connected.
    private val http = scope.async(start = CoroutineStart.LAZY) {
        HttpClient {
            defaultRequest {
                if (DeviceInfo.host.platform == DevicePlatform.ANDROID) {
                    url(host = "10.0.2.2", port = serverUrl.port)
                } else {
                    url.takeFrom(serverUrl)
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
            sendClientConnectMessage()
            awaitDesktopConnectMessage()
            sendOutgoingMessages()
            awaitIncomingMessages()
        }
    }

    private suspend fun DefaultClientWebSocketSession.awaitDesktopConnectMessage() {
        val (deviceInfo, pluginIds) = receiveDeserialized<DesktopConnectMessage>()
    }

    private suspend fun DefaultClientWebSocketSession.sendClientConnectMessage() {
        sendSerialized(
            ClientConnectMessage(
                sessionId = sessionId,
                deviceInfo = DeviceInfo.host,
                pluginIds = pluginMap.keys.toList(),
            ),
        )
    }

    private fun DefaultClientWebSocketSession.sendOutgoingMessages() {
        plugins.forEach { plugin ->
            plugin.outgoingMessages
                .onEach { message ->
                    val messageContent = Json.encodeToString(serializer(plugin.clientMessageType), message)
                    val pluginMessage = PluginMessage(
                        pluginId = plugin.id,
                        content = messageContent,
                    )
                    sendSerialized(pluginMessage)
                }.launchIn(this)
        }
    }

    private suspend fun DefaultClientWebSocketSession.awaitIncomingMessages() {
        launch {
            while (isActive) {
                try {
                    val (pluginId, content) = receiveDeserialized<PluginMessage>()
                    val plugin = pluginMap[pluginId] ?: continue
                    val deserialized = Json.decodeFromString(serializer(plugin.serverMessageType), content)
                    @Suppress("UNCHECKED_CAST")
                    (plugin as ClientPlugin<Any, Any>).handleMessage(deserialized!!)
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }
        }
        closeReason.await()
    }
}
