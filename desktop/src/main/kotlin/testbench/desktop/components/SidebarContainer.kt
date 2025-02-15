package testbench.desktop.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import testbench.desktop.LocalSessionHolder
import testbench.desktop.server.SessionData
import testbench.plugin.desktop.DesktopPlugin
import testbench.plugin.desktop.UiHookLocation
import testbench.ui.TestbenchIcon
import testbench.ui.TestbenchTheme
import testbench.ui.testbench
import testbench.ui.thenIf

@Composable
fun SidebarContainer(
    activeSession: SessionData,
    onSessionSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    onPluginSelected: (DesktopPlugin<*, *>) -> Unit,
) {
    val sessions by LocalSessionHolder.current.sessions.collectAsState()
    Box(
        modifier = modifier
            .background(TestbenchTheme.colors.background),
    ) {
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState),
        ) {
            testbench.Text(
                text = "Session",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = TestbenchTheme.textStyles.h4,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .padding(horizontal = 8.dp),
            )

            SessionSelector(
                activeSession = activeSession,
                sessions = sessions,
                onSessionSelected = onSessionSelected,
            )

            testbench.HorizontalDivider()

            testbench.Text(
                text = "Plugins",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = TestbenchTheme.textStyles.h4,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .padding(horizontal = 8.dp),
            )

            remember(activeSession) {
                activeSession
                    .pluginRegistry
                    .enabledPlugins
                    .filterValues { it.ui.containsKey(UiHookLocation.MAIN_PANEL) }
            }.forEach { (_, plugin) ->
                PluginRow(
                    name = plugin.name,
                    icon = TestbenchIcon.forName(plugin.pluginIcon),
                    onClick = { onPluginSelected(plugin) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            if (activeSession.pluginRegistry.disabledPlugins.isNotEmpty()) {
                testbench.Text(
                    text = "Disabled",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = TestbenchTheme.textStyles.h4,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .padding(horizontal = 8.dp),
                )
            }

            activeSession.pluginRegistry
                .disabledPlugins
                .forEach { (plugin, _) ->
                    PluginRow(
                        name = plugin.name,
                        icon = TestbenchIcon.forName(plugin.pluginIcon),
                        enabled = false,
                        onClick = { onPluginSelected(plugin) },
                        modifier = Modifier
                            .fillMaxWidth(),
                    )
                }
        }

        VerticalScrollbar(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight(),
            adapter = rememberScrollbarAdapter(scrollState),
        )
    }
}

@Composable
private fun PluginRow(
    name: String,
    icon: TestbenchIcon,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Row(
        modifier = modifier
            .thenIf(enabled) { clickable(onClick = onClick) }
            .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(20.dp)
                .background(
                    TestbenchTheme.colors.primary,
                    shape = RoundedCornerShape(4.dp),
                ),
            contentAlignment = Alignment.Center,
        ) {
            testbench.Icon(
                icon = icon,
                modifier = Modifier.size(14.dp),
                tint = TestbenchTheme.colors.onPrimary,
            )
        }

        testbench.Text(name)
    }
}
