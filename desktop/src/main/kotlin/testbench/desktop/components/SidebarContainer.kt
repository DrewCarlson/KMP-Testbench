package testbench.desktop.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.component.Icon
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.theme.colorPalette
import testbench.desktop.resources.TestBenchIcons

private val TEMP_PLUGINS = listOf(
    "Network" to "icons/globe.svg",
    "Databases" to "icons/database.svg",
    "Preferences" to "icons/plugin.svg",
    "Logs" to "icons/plugin.svg",
)

@Composable
fun SidebarContainer(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .background(JewelTheme.globalColors.panelBackground)
    ) {

        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Spacer(modifier = Modifier.size(0.dp))

            TEMP_PLUGINS.forEach { (name, icon) ->
                PluginRow(
                    name = name,
                    icon = icon,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.size(0.dp))
        }

        VerticalScrollbar(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight(),
            adapter = rememberScrollbarAdapter(scrollState)
        )
    }
}

@Composable
private fun PluginRow(
    name: String,
    icon: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .padding(horizontal = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier.size(20.dp)
                .background(JewelTheme.colorPalette.blue(4), shape = RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                icon,
                null,
                TestBenchIcons::class.java,
                modifier = Modifier.size(14.dp)
            )
        }

        Text(
            text = name,
        )
    }
}
