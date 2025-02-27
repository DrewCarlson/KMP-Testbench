package testbench.desktop

import androidx.compose.runtime.*
import androidx.compose.ui.window.application
import testbench.desktop.server.SessionHolder
import testbench.desktop.server.TestbenchServer
import testbench.desktop.window.MainWindow
import testbench.ui.TestbenchTheme

val LocalSessionHolder = compositionLocalOf<SessionHolder> { error("SessionHolder not found") }

fun main() = application {
    val sessionHolder = remember { SessionHolder() }
    val server = remember { TestbenchServer(sessionHolder) }

    LaunchedEffect(Unit) {
        server.setupServer(8182)
        server.startSever()
    }

    CompositionLocalProvider(
        LocalSessionHolder provides sessionHolder,
    ) {
        val sessions by sessionHolder.sessions.collectAsState()
        val activeSession by sessionHolder.activeSession.collectAsState()
        TestbenchTheme {
            MainWindow(
                activeSession = activeSession,
                sessions = sessions,
                onSessionSelected = sessionHolder::setActiveSession,
                onCloseRequest = {
                    server.stopServer()
                    exitApplication()
                },
            )
        }
    }
}
