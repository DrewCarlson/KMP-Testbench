package testbench.desktop

import androidx.compose.runtime.*
import androidx.compose.ui.window.application
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.intui.standalone.theme.IntUiTheme
import org.jetbrains.jewel.intui.standalone.theme.darkThemeDefinition
import org.jetbrains.jewel.intui.standalone.theme.default
import org.jetbrains.jewel.intui.window.decoratedWindow
import org.jetbrains.jewel.intui.window.styling.dark
import org.jetbrains.jewel.ui.ComponentStyling
import org.jetbrains.jewel.window.styling.TitleBarStyle
import testbench.desktop.server.TestBenchServer
import testbench.desktop.window.MainWindow
import testbench.testbench.desktop.server.SessionHolder

val LocalSessionHolder = compositionLocalOf<SessionHolder> { error("SessionHolder not found") }
val LocalTestBenchServer = compositionLocalOf<TestBenchServer> { error("TestBenchServer not found!") }

fun main() = application {
    val sessionHolder = remember { SessionHolder() }
    val server = remember { TestBenchServer(sessionHolder) }

    LaunchedEffect(Unit) {
        server.setupServer(8182)
        server.startSever()
    }

    CompositionLocalProvider(
        LocalSessionHolder provides sessionHolder,
        LocalTestBenchServer provides server,
    ) {
        IntUiTheme(
            theme = JewelTheme.darkThemeDefinition(),
            styling = ComponentStyling.default().decoratedWindow(
                titleBarStyle = TitleBarStyle.dark(),
            ),
        ) {
            val sessions by sessionHolder.sessions.collectAsState()
            val activeSession by sessionHolder.activeSession.collectAsState()
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
