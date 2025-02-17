package testbench.tests

import app.cash.turbine.test
import app.cash.turbine.turbineScope
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable
import testbench.client.ReconnectHandler
import testbench.client.TestbenchClient
import testbench.desktop.server.SessionHolder
import testbench.desktop.server.TestbenchServer
import testbench.plugin.client.ClientPlugin
import testbench.plugin.desktop.DesktopPlugin
import kotlin.reflect.KType
import kotlin.reflect.typeOf
import kotlin.test.*

class CommunicationTests {
    private lateinit var sessionHolder: SessionHolder
    private lateinit var clientPlugin: TestClientPlugin
    private lateinit var desktopPlugin: TestDesktopPlugin
    private lateinit var server: TestbenchServer
    private lateinit var client: TestbenchClient

    fun setup(backgroundScope: CoroutineScope) {
        clientPlugin = TestClientPlugin()
        desktopPlugin = TestDesktopPlugin()
        sessionHolder = SessionHolder()
        server = TestbenchServer(
            sessionHolder = sessionHolder,
            plugins = listOf(desktopPlugin),
        )
        server.setupServer(48345)
        client = TestbenchClient(
            plugins = listOf(clientPlugin),
            autoConnect = false,
            reconnectHandler = ReconnectHandler.never(),
            coroutineContext = backgroundScope.coroutineContext,
            serverUrl = Url("http://127.0.0.1:48345"),
        )
    }

    @AfterTest
    fun after() {
        client.disable()
        server.stopServer()
    }

    suspend fun awaitConnect() {
        server.startSever()
        server.serverStartedFlow.first { it }
        client.enable()
        client.isConnectedFlow.first { it }
    }

    @Test
    fun test_client_connect_to_server() = runTest {
        setup(backgroundScope)
        server.startSever()
        server.serverStartedFlow.test {
            assertTrue(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        client.isConnectedFlow.test {
            assertFalse(awaitItem())
            client.enable()
            assertTrue(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun test_desktop_handles_client_message() = runTest {
        setup(backgroundScope)
        awaitConnect()

        desktopPlugin.receivedMessages.test {
            val expected = ClientTestMessage.IntMessage(0)
            clientPlugin.sendMessage(expected)
            val actual = awaitItem()
            assertEquals(expected, actual)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun test_client_handles_desktop_message() = runTest {
        setup(backgroundScope)
        awaitConnect()

        clientPlugin.receivedMessages.test {
            val expected = DesktopTestMessage.IntMessage(0)
            desktopPlugin.sendMessage(expected)
            val actual = awaitItem()
            assertEquals(expected, actual)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun test_ping_pong() = runTest {
        setup(backgroundScope)
        awaitConnect()

        turbineScope {
            val desktopReceived = desktopPlugin.receivedMessages.testIn(this)
            val clientReceived = clientPlugin.receivedMessages.testIn(this)

            clientPlugin.sendMessage(ClientTestMessage.Ping)

            assertEquals(ClientTestMessage.Ping, desktopReceived.awaitItem())
            assertEquals(DesktopTestMessage.Pong, clientReceived.awaitItem())

            desktopReceived.cancelAndIgnoreRemainingEvents()
            clientReceived.cancelAndIgnoreRemainingEvents()
        }
    }

    @Serializable
    sealed class ClientTestMessage {
        @Serializable
        data class IntMessage(
            val int: Int,
        ) : ClientTestMessage()

        @Serializable
        data object Ping : ClientTestMessage()
    }

    @Serializable
    sealed class DesktopTestMessage {
        @Serializable
        data class IntMessage(
            val int: Int,
        ) : DesktopTestMessage()

        @Serializable
        data object Pong : DesktopTestMessage()
    }

    inner class TestDesktopPlugin : DesktopPlugin<DesktopTestMessage, ClientTestMessage> {
        override val id: String = "test-plugin"
        override val name: String = "Test Plugin"

        override val serverMessageType: KType = typeOf<DesktopTestMessage>()
        override val clientMessageType: KType = typeOf<ClientTestMessage>()

        val receivedMessages = MutableSharedFlow<ClientTestMessage>(
            extraBufferCapacity = Int.MAX_VALUE,
            onBufferOverflow = BufferOverflow.DROP_OLDEST,
        )
        private val messageQueue = Channel<DesktopTestMessage>(
            capacity = Int.MAX_VALUE,
            onBufferOverflow = BufferOverflow.DROP_OLDEST,
        )
        override val outgoingMessages: Flow<DesktopTestMessage> = messageQueue.receiveAsFlow()

        override suspend fun handleMessage(message: ClientTestMessage) {
            receivedMessages.emit(message)
            if (message is ClientTestMessage.Ping) {
                messageQueue.send(DesktopTestMessage.Pong)
            }
        }

        suspend fun sendMessage(message: DesktopTestMessage) {
            messageQueue.send(message)
        }
    }

    inner class TestClientPlugin : ClientPlugin<DesktopTestMessage, ClientTestMessage> {
        override val id: String = "test-plugin"
        override val name: String = "Test Plugin"

        override val serverMessageType: KType = typeOf<DesktopTestMessage>()
        override val clientMessageType: KType = typeOf<ClientTestMessage>()

        val receivedMessages = MutableSharedFlow<DesktopTestMessage>(
            extraBufferCapacity = Int.MAX_VALUE,
            onBufferOverflow = BufferOverflow.DROP_OLDEST,
        )
        private val messageQueue = Channel<ClientTestMessage>(
            capacity = Int.MAX_VALUE,
            onBufferOverflow = BufferOverflow.DROP_OLDEST,
        )
        override val outgoingMessages: Flow<ClientTestMessage> = messageQueue.receiveAsFlow()

        override suspend fun handleMessage(message: DesktopTestMessage) {
            receivedMessages.emit(message)
        }

        suspend fun sendMessage(message: ClientTestMessage) {
            messageQueue.send(message)
        }
    }
}
