package testbench.desktop.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.component.Icon
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.theme.colorPalette
import org.jetbrains.jewel.ui.util.thenIf
import testbench.desktop.resources.TestBenchIcons
import testbench.desktop.server.SessionData
import testbench.plugin.desktop.DesktopPlugin
import testbench.plugin.desktop.UiHookLocation

@Composable
fun SidebarContainer(
    activeSession: SessionData,
    modifier: Modifier = Modifier,
    onPluginSelected: (DesktopPlugin<*, *>) -> Unit,
) {
    Box(
        modifier = modifier
            .background(JewelTheme.globalColors.panelBackground),
    ) {
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .verticalScroll(scrollState),
        ) {
            Text(
                text = "Plugins",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier
                    .padding(8.dp),
            )

            remember(activeSession) {
                activeSession
                    .pluginRegistry
                    .enabledPlugins
                    .filterValues { it.uiHooks.containsKey(UiHookLocation.MAIN_PANEL) }
            }.forEach { (_, plugin) ->
                PluginRow(
                    name = plugin.name,
                    icon = "icons/${plugin.pluginIcon}.svg",
                    onClick = { onPluginSelected(plugin) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            if (activeSession.pluginRegistry.disabledPlugins.isNotEmpty()) {
                Text(
                    text = "Disabled",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .padding(8.dp),
                )
            }

            activeSession.pluginRegistry
                .disabledPlugins
                .forEach { (plugin, _) ->
                    PluginRow(
                        name = plugin.name,
                        icon = "icons/${plugin.pluginIcon}.svg",
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
    icon: String,
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
                    if (enabled) {
                        JewelTheme.colorPalette.blue(4)
                    } else {
                        JewelTheme.colorPalette.grey(4)
                    },
                    shape = RoundedCornerShape(4.dp),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                icon,
                null,
                TestBenchIcons::class.java,
                modifier = Modifier.size(14.dp),
            )
        }

        Text(
            text = name,
        )
    }
}
