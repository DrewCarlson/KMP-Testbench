import androidx.compose.runtime.LaunchedEffect
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
import testbench.desktop.server.TestBenchServer
import testbench.desktop.window.MainWindow
import testbench.plugins.network.NetworkServerPlugin

val networkPlugin = NetworkServerPlugin()

fun main() = application {

    val server = remember {
        TestBenchServer(
            plugins = listOf(
                networkPlugin
            )
        )
    }

    LaunchedEffect(Unit) {
        server.setupServer(8182)
        server.startSever()
    }

    IntUiTheme(
        theme = JewelTheme.darkThemeDefinition(),
        styling = ComponentStyling.default().decoratedWindow(
            titleBarStyle = TitleBarStyle.dark()
        )
    ) {
        MainWindow(::exitApplication)
    }
}
