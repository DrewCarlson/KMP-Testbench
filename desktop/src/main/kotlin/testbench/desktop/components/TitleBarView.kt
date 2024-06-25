package testbench.desktop.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.jewel.ui.component.*
import org.jetbrains.jewel.ui.painter.hints.Size
import org.jetbrains.jewel.ui.painter.rememberResourcePainterProvider
import org.jetbrains.jewel.window.DecoratedWindowScope
import org.jetbrains.jewel.window.TitleBar
import org.jetbrains.jewel.window.newFullscreenControls
import testbench.desktop.resources.TestBenchIcons
import java.awt.Desktop
import java.net.URI

@Composable
fun DecoratedWindowScope.TitleBarView() {
    TitleBar(
        modifier = Modifier.newFullscreenControls(),
    ) {
        Row(Modifier.align(Alignment.Start)) {
            Dropdown(Modifier.height(30.dp), menuContent = {
                /*MainViewModel.views.forEach {
                    selectableItem(
                        selected = MainViewModel.currentView == it,
                        onClick = {
                            MainViewModel.currentView = it
                        },
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            val painterProvider =
                                rememberResourcePainterProvider(it.icon, StandaloneSampleIcons::class.java)
                            val painter by painterProvider.getPainter(Size(20))
                            Icon(painter, "icon", modifier = Modifier.size(20.dp))
                            Text(it.title)
                        }
                    }
                }*/
            }) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        val painterProvider =
                            rememberResourcePainterProvider(
                                "icons/app_icon.svg",
                                TestBenchIcons::class.java,
                            )
                        val painter by painterProvider.getPainter(Size(20))
                        Icon(painter, "icon")
                        Text("Test")
                    }
                }
            }
        }

        Text(title)

        Row(Modifier.align(Alignment.End)) {
            Tooltip({
                Text("Open Jewel Github repository")
            }) {
                IconButton(
                    modifier = Modifier
                        .size(40.dp)
                        .padding(5.dp),
                    onClick = { Desktop.getDesktop().browse(URI.create("https://github.com/squareup/KMP-Test-Bench")) },
                ) {
                    Icon(
                        "icons/github@20x20_dark.svg",
                        "Github",
                        TestBenchIcons::class.java,
                    )
                }
            }

            /*Tooltip({
                when (MainViewModel.theme) {
                    IntUiThemes.Light -> Text("Switch to light theme with light header")
                    IntUiThemes.LightWithLightHeader -> Text("Switch to dark theme")
                    IntUiThemes.Dark, IntUiThemes.System -> Text("Switch to light theme")
                }
            }) {
                IconButton({
                    MainViewModel.theme =
                        when (MainViewModel.theme) {
                            IntUiThemes.Light -> IntUiThemes.LightWithLightHeader
                            IntUiThemes.LightWithLightHeader -> IntUiThemes.Dark
                            IntUiThemes.Dark, IntUiThemes.System -> IntUiThemes.Light
                        }
                }, Modifier.size(40.dp).padding(5.dp)) {
                    when (MainViewModel.theme) {
                        IntUiThemes.Light ->
                            Icon(
                                "icons/lightTheme@20x20.svg",
                                "Themes",
                                StandaloneSampleIcons::class.java,
                            )

                        IntUiThemes.LightWithLightHeader ->
                            Icon(
                                "icons/lightWithLightHeaderTheme@20x20.svg",
                                "Themes",
                                StandaloneSampleIcons::class.java,
                            )

                        IntUiThemes.Dark ->
                            Icon(
                                "icons/darkTheme@20x20.svg",
                                "Themes",
                                StandaloneSampleIcons::class.java,
                            )

                        IntUiThemes.System ->
                            Icon(
                                "icons/systemTheme@20x20.svg",
                                "Themes",
                                StandaloneSampleIcons::class.java,
                            )
                    }
                }
            }*/
        }
    }
}
