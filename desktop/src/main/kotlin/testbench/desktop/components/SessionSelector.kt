package testbench.desktop.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import testbench.desktop.server.SessionData
import testbench.ui.TestbenchIcon
import testbench.ui.TestbenchTheme
import testbench.ui.components.ButtonType
import testbench.ui.testbench

@Composable
fun SessionSelector(
    activeSession: SessionData,
    sessions: Map<String, SessionData>,
    onSessionSelected: (sessionId: String) -> Unit,
) {
    Box(
        modifier = Modifier
            .height(40.dp)
            .fillMaxWidth()
            .background(TestbenchTheme.colors.surface)
    ) {
        testbench.Dropdown(
            modifier = Modifier.fillMaxWidth(),
            menu = { close ->
                if (sessions.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        testbench.Text(
                            text = "waiting for session",
                            style = TestbenchTheme.textStyles.h4,
                        )
                    }
                }
                sessions.forEach { (_, session) ->
                    testbench.TextButton(
                        onClick = {
                            onSessionSelected(session.sessionId)
                            close()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        buttonType = if (session.sessionId == activeSession.sessionId) {
                            ButtonType.PRIMARY
                        } else {
                            ButtonType.STANDARD
                        }
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.Start),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            DeviceInfoIcon(
                                deviceInfo = session.deviceInfo,
                                isConnected = session.isConnected,
                            )
                            testbench.Text(
                                text = session.title,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
            },
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(8.dp)
            ) {
                if (activeSession.isDefault) {
                    DeviceInfoIcon(
                        deviceInfo = activeSession.deviceInfo
                    )
                    testbench.Text(
                        text = "(idle)",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    DeviceInfoIcon(
                        deviceInfo = activeSession.deviceInfo,
                        isConnected = activeSession.isConnected
                    )
                    testbench.Text(
                        text = activeSession.title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }
                testbench.Icon(
                    TestbenchIcon.CHEVRON_RIGHT,
                    modifier = Modifier.rotate(90f)
                )
            }
        }


        //Row(Modifier.align(Alignment.TopEnd)) {
        /*Tooltip({
            Text("Open Github repository")
        }) {
            IconButton(
                modifier = Modifier
                    .size(40.dp)
                    .padding(5.dp),
                onClick = { Desktop.getDesktop().browse(URI.create("https://github.com/squareup/KMP-Testbench")) },
            ) {
                Icon(
                    PathIconKey(
                        "icons/github@20x20_dark.svg",
                        TestBenchIcons::class.java,
                    ),
                    contentDescription = "Github",
                )
            }
        }*/

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
