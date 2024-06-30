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
import testbench.communication.PluginMessage
import testbench.desktop.plugins.PluginRegistry
import testbench.plugin.server.ServerPlugin
import testbench.testbench.desktop.server.SessionData
import testbench.testbench.desktop.server.SessionHolder
import java.time.Duration

class TestBenchServer(
    private val sessionHolder: SessionHolder,
) {
    private lateinit var server: ApplicationEngine
    private val serverStarted = MutableStateFlow(false)

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
                logger = LoggerFactory.getLogger("TestBenchServer")
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
                        connectMessage.sessionId,
                        update = { it.copy(isConnected = true) },
                    ) {
                        SessionData(
                            sessionId = connectMessage.sessionId,
                            isConnected = true,
                            pluginRegistry = PluginRegistry(),
                            deviceInfo = connectMessage.deviceInfo,
                        )
                    }

                    if (sessionHolder.activeSession.value.isDefault) {
                        sessionHolder.setActiveSession(session.sessionId)
                        sessionHolder.remove("default")
                    }

                    try {
                        while (isActive) {
                            val message = receiveDeserialized<PluginMessage>()
                            dispatchPluginMessage(session, message)
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

    private fun dispatchPluginMessage(
        session: SessionData,
        message: PluginMessage,
    ) {
        @Suppress("UNCHECKED_CAST")
        val plugin = session.pluginRegistry.plugins[message.pluginId] as? ServerPlugin<Any, Any>
        if (plugin != null) {
            val content =
                Json.decodeFromString(serializer(plugin.serverMessageType), message.content)
            plugin.handleMessage(
                checkNotNull(content) {
                    "Failed to process message (plugin:${plugin.id}): ${message.content}"
                },
            )
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
