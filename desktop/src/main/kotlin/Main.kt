package testbench

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.window.application
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.intui.standalone.theme.IntUiTheme
import org.jetbrains.jewel.intui.standalone.theme.darkThemeDefinition
import org.jetbrains.jewel.intui.standalone.theme.default
import org.jetbrains.jewel.intui.window.decoratedWindow
import org.jetbrains.jewel.intui.window.styling.dark
import org.jetbrains.jewel.ui.ComponentStyling
import org.jetbrains.jewel.window.styling.TitleBarStyle
import testbench.desktop.plugins.PluginRegistry
import testbench.desktop.server.TestBenchServer
import testbench.desktop.window.MainWindow

val LocalPluginRegistry = compositionLocalOf<PluginRegistry> { error("PluginRegistry not found!") }
val LocalTestBenchServer = compositionLocalOf<TestBenchServer> { error("TestBenchServer not found!") }

fun main() = application {
    val pluginRegistry = remember {
        PluginRegistry()
    }

    val server = remember { TestBenchServer(pluginRegistry) }

    LaunchedEffect(Unit) {
        server.setupServer(8182)
        server.startSever()
    }

    CompositionLocalProvider(
        LocalPluginRegistry provides pluginRegistry,
        LocalTestBenchServer provides server,
    ) {
        IntUiTheme(
            theme = JewelTheme.darkThemeDefinition(),
            styling = ComponentStyling.default().decoratedWindow(
                titleBarStyle = TitleBarStyle.dark(),
            ),
        ) {
            MainWindow(
                onCloseRequest = {
                    server.stopServer()
                    exitApplication()
                },
            )
        }
    }
}
