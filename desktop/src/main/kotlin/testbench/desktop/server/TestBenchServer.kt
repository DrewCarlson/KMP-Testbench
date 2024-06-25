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
import io.ktor.util.logging.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import org.slf4j.event.Level
import testbench.communication.PluginMessage
import testbench.plugin.server.ServerPlugin
import java.time.Duration

class TestBenchServer(
    plugins: List<ServerPlugin>
) {

    private var plugins = plugins.associateBy { it.id }
    private lateinit var server: ApplicationEngine
    private var _serverStarted = MutableStateFlow(false)

    val serverStarted = _serverStarted.asStateFlow()

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
                webSocket("/plugin") {
                    println("Client connected")
                    incoming.receiveAsFlow()
                        .collect { frame ->
                            println("Frame: $frame")
                            if (frame is Frame.Text) {
                                val message = Json.decodeFromString<PluginMessage>(frame.readText())

                                println(plugins)
                                println("Message received: $message")
                                plugins[message.pluginId]?.handleMessage(message.content)
                            }
                        }
                }
            }
        }
    }

    fun startSever() {
        check(::server.isInitialized) { "setupServer(port) must be called before startServer()" }

        server = server.start(wait = false)
        _serverStarted.update { true }
    }

    fun stopServer() {
        check(serverStarted.value) { "startServer() must be called before stopServer()" }

        server.stop()
        _serverStarted.update { false }
    }

}

