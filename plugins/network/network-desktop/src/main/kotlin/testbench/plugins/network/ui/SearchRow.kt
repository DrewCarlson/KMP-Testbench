package testbench.plugins.network.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.jewel.ui.component.*
import org.jetbrains.jewel.ui.icon.PathIconKey
import testbench.plugins.network.NetworkDesktopPlugin

@Composable
public fun SearchRow(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onClearEntries: () -> Unit,
) {
    val textFieldState = rememberTextFieldState(searchQuery)
    LaunchedEffect(textFieldState.text) {
        onSearchQueryChange(textFieldState.text.toString())
    }
    Row(
        modifier = Modifier.padding(horizontal = 6.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextField(
            modifier = Modifier.weight(1f, fill = true),
            state = textFieldState,
            placeholder = {
                Text(
                    text = "Search...",
                    maxLines = 1,
                )
            },
            leadingIcon = {
                Icon(
                    key = PathIconKey(
                        "icons/search_dark.svg",
                        NetworkDesktopPlugin::class.java,
                    ),
                    contentDescription = null,
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
