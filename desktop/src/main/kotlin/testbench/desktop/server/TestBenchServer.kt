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
import io.ktor.websocket.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import org.slf4j.event.Level
import testbench.communication.PluginMessage
import testbench.desktop.plugins.PluginRegistry
import java.time.Duration

class TestBenchServer(
    private val pluginRegistry: PluginRegistry,
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
                    incoming
                        .receiveAsFlow()
                        .filterIsInstance<Frame.Text>()
                        .dispatchPluginMessages()
                }
            }
        }
    }

    private suspend fun Flow<Frame.Text>.dispatchPluginMessages() {
        collect { frame ->
            val message = Json.decodeFromString<PluginMessage>(frame.readText())
            val plugin = pluginRegistry.plugins[message.pluginId] ?: return@collect
            plugin.handleMessage(message.content)
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
