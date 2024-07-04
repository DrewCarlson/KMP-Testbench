package testbench.plugins.network.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.jewel.ui.component.*
import testbench.plugins.network.NetworkDesktopPlugin

@Composable
public fun SearchRow(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onClearEntries: () -> Unit,
) {
    Row(
        modifier = Modifier.padding(horizontal = 6.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextField(
            modifier = Modifier.weight(1f, fill = true),
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = {
                Text(
                    text = "Search...",
                    maxLines = 1,
                )
            },
            leadingIcon = {
                Icon(
                    resource = "icons/search_dark.svg",
                    contentDescription = null,
                    iconClass = NetworkDesktopPlugin::class.java,
                    modifier = Modifier.size(16.dp),
                )
            },
        )
        OutlinedButton(
            onClick = onClearEntries,
        ) {
            Text(
                text = "Clear",
                maxLines = 1,
            )
        }
    }
}
