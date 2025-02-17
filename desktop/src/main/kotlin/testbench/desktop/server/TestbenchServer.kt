package testbench.desktop.server

import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.slf4j.LoggerFactory
import org.slf4j.event.Level
import testbench.communication.ClientConnectMessage
import testbench.communication.DesktopConnectMessage
import testbench.communication.PluginMessage
import testbench.desktop.plugins.PluginRegistry
import testbench.desktop.plugins.PluginRegistry.Companion.loadPlugins
import testbench.device.DeviceInfo
import testbench.plugin.desktop.DesktopPlugin
import java.time.Duration

class TestbenchServer(
    private val sessionHolder: SessionHolder,
    private val plugins: List<DesktopPlugin<*, *>> = loadPlugins(),
) {
    private lateinit var server: ApplicationEngine
    private val serverStarted = MutableStateFlow(false)
    val serverStartedFlow = serverStarted.asStateFlow()

    fun setupServer(port: Int) {
        check(!serverStarted.value) { "Server is already running, stopServer() must be called first." }

        server = embeddedServer(CIO, port = port) {
            install(CORS) {
                methods.addAll(HttpMethod.DefaultMethods)
                allowNonSimpleContentTypes = true
                allowHeaders { true }
                anyHost()
            }
            install(CallLogging) {
                level = Level.TRACE
                logger = LoggerFactory.getLogger("TestbenchServer")
            }
            install(ContentNegotiation) {
                json(Json)
            }
            install(WebSockets) {
                pingPeriod = Duration.ofSeconds(60)
                timeout = Duration.ofSeconds(15)
                contentConverter = KotlinxWebsocketSerializationConverter(Json)
            }
            routing {
                webSocket {
                    val connectMessage = receiveDeserialized<ClientConnectMessage>()

                    val session = sessionHolder.updateOrCreate(
                        id = connectMessage.sessionId,
                        update = { it.copy(isConnected = true) },
                    ) {
                        SessionData(
                            sessionId = connectMessage.sessionId,
                            isConnected = true,
                            pluginRegistry = PluginRegistry(
                                clientPluginIds = connectMessage.pluginIds,
                                plugins = plugins,
                            ),
                            deviceInfo = connectMessage.deviceInfo,
                        )
                    }

                    if (sessionHolder.activeSession.value.isDefault) {
                        sessionHolder.setActiveSession(session.sessionId)
                        sessionHolder.remove("default")
                    }

                    sendSerialized(
                        DesktopConnectMessage(
                            deviceInfo = DeviceInfo.host,
                            pluginIds = session.pluginRegistry
                                .enabledPlugins
                                .keys
                                .toList(),
                        ),
                    )

                    val plugins = session.pluginRegistry.enabledPlugins.values
                    plugins.forEach { plugin ->
                        plugin.outgoingMessages
                            .onEach { message ->
                                val serializer = serializer(plugin.serverMessageType)
                                val messageContent = Json.encodeToString(serializer, message)
                                val pluginMessage = PluginMessage(
                                    pluginId = plugin.id,
                                    content = messageContent,
                                )
                                sendSerialized(pluginMessage)
                            }.catch { error ->
                                call.application.log.error(
                                    "Failed to process outgoing message: plugin=${plugin.id}",
                                    error,
                                )
                            }.launchIn(this)
                    }

                    try {
                        while (isActive) {
                            val message = receiveDeserialized<PluginMessage>()
                            session.pluginRegistry.handleMessage(message)
                        }
                    } finally {
                        sessionHolder.update(connectMessage.sessionId) {
                            it.copy(isConnected = false)
                        }
                    }
                }
            }
        }
    }

    fun startSever() {
        check(::server.isInitialized) { "setupServer(port) must be called before startServer()" }

        server = server.start(wait = false)
        serverStarted.update { true }
    }

    fun stopServer() {
        if (!serverStarted.value) {
            return
        }
        server.stop()
        serverStarted.update { false }
    }
}
